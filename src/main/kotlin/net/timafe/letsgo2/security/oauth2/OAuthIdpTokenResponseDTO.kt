package net.timafe.letsgo2.security.oauth2

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

open class OAuthIdpTokenResponseDTO(
    @JsonProperty("token_type")
    var tokenType: String,

    var scope: String,

    @JsonProperty("expires_in")
    var expiresIn: Long,

    @JsonProperty("ext_expires_in")
    var extExpiresIn: Long,

    @JsonProperty("expires_on")
    var expiresOn: Long,

    @JsonProperty("not-before-policy")
    var notBefore: Long,

    var resource: UUID,

    @JsonProperty("access_token")
    var accessToken: String,

    @JsonProperty("refresh_token")
    var refreshToken: String,

    @JsonProperty("id_token")
    var idToken: String,

    @JsonProperty("session_state")
    var sessionState: String,

    @JsonProperty("refresh_expires_in")
    var refreshExpiresIn: String
)
