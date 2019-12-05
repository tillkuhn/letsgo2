package net.timafe.letsgo2
import net.minidev.json.JSONArray
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
class QuickUnitTest {

    val IAM_ROLE_PATTERN = "cognito-role-"

    @Test
    fun parseCognitoRoles() {
        val list = JSONArray()
        // cognito:preferred_role
        list.add("arn:aws:iam::xxxxxxxxxx:role/somethinglese-role-admin")
        list.add("arn:aws:iam::xxxxxxxxxx:role/letsgo2-cognito-role-admin")
        list.add("arn:aws:iam::xxxxxxxxxx:role/letsgo2-cognito-role-user")
        list.add("arn:aws:iam::xxxxxxxxxx:role/letsgo2-cognito-role-guest")
        val cognitoroles = list.filter { it.toString().contains(IAM_ROLE_PATTERN) }
        assertThat(cognitoroles.size).isEqualTo(3)
        val extratedRoles = cognitoroles.map { it.toString().substring(it.toString().indexOf(IAM_ROLE_PATTERN)+IAM_ROLE_PATTERN.length).toUpperCase() }
        extratedRoles.forEach {
            assertThat(! it.contains("arn"))
            assertThat(! it.contains(IAM_ROLE_PATTERN))
        }

    }
}
