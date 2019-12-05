@file:JvmName("SecurityUtils")

package net.timafe.letsgo2.security

import net.minidev.json.JSONArray
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*

/**
 * Get the login of the current user.
 *
 * @return the login of the current user.
 */
fun getCurrentUserLogin(): Optional<String> =
    Optional.ofNullable(SecurityContextHolder.getContext().authentication)
        .map { authentication ->
            when (val principal = authentication.principal) {
                is UserDetails -> principal.username
                is JwtAuthenticationToken -> (authentication as JwtAuthenticationToken).token.claims as String
                is DefaultOidcUser -> {

                    // principal.idToken.claims.get("cognito:username") = mail address
                    // principal.idToken.claims.get("cognito:preferred_role") = arn:aws:iam::062960202541:role/cognito-empty-role-test
                    if (principal.idToken.claims.containsKey("cognito:username")) {
                        principal.idToken.claims.get("cognito:username").toString()
                    } else if (principal.attributes.containsKey("preferred_username")) {
                        principal.attributes["preferred_username"].toString()
                    } else {
                        null
                    }
                }
                is String -> principal
                else -> null
            }
        }

/**
 * Check if a user is authenticated.
 *
 * @return true if the user is authenticated, false otherwise.
 */
fun isAuthenticated(): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
        return getAuthorities(authentication).none { it == ANONYMOUS }
    }

    return false
}

/**
 * If the current user has a specific authority (security role).
 *
 * The name of this method comes from the `isUserInRole()` method in the Servlet API
 *
 * @param authority the authority to check.
 * @return true if the current user has the authority, false otherwise.
 */
fun isCurrentUserInRole(authority: String): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication

    if (authentication != null) {
        return getAuthorities(authentication).any { it == authority }
    }

    return false
}

fun getAuthorities(authentication: Authentication): List<String> {
    val authorities = when (authentication) {
        is JwtAuthenticationToken ->
            extractAuthorityFromClaims(authentication.token.claims)
        else ->
            authentication.authorities
    }
    return authorities
        .map(GrantedAuthority::getAuthority)
}

fun extractAuthorityFromClaims(claims: Map<String, Any>): List<GrantedAuthority> {
    return mapRolesToGrantedAuthorities(getRolesFromClaims(claims))
}

// take a list of simple names role strings, and map it into a list of GrantedAuthority objects if pattern machtes
fun mapRolesToGrantedAuthorities(roles: Collection<String>): List<GrantedAuthority> {
    return roles
        .filterIndexed() {  index,rolename -> rolename.startsWith("ROLE_") }
        .map { SimpleGrantedAuthority(it) }
}

@Suppress("UNCHECKED_CAST")
fun getRolesFromClaims(claims: Map<String, Any>): Collection<String> {
    return if (claims.containsKey("cognito:roles")) {
        when (val coros = claims.get("cognito:roles")) {
            is JSONArray -> extractRolesFromJSONArray(coros)
            else -> listOf<String>()
        }
    } else {
        listOf<String>()
    }
    // claims.get("cognito:roles") = JSONArray of arns
    // cognito:preferred_role -> arn:aws:iam::xxxxxxx:role/cognito-empty-role-test
    // return listOf<String>("ROLE_USER", "ROLE_ADMIN")
    // return claims.getOrDefault("groups", claims.getOrDefault("roles", listOf<String>())) as Collection<String>
}

fun extractRolesFromJSONArray(jsonArray: JSONArray): List<String> {
    val iamRolePattern = "cognito-role-"
    return jsonArray
        .filter { it.toString().contains(iamRolePattern) }
        .map { "ROLE_" + it.toString().substring(it.toString().indexOf(iamRolePattern)+iamRolePattern.length).toUpperCase() }

}

