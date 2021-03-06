package net.timafe.letsgo2.service.mapper

import kotlin.test.assertNotNull
import kotlin.test.assertNull
import net.timafe.letsgo2.Letsgo2App
import net.timafe.letsgo2.config.TestSecurityConfiguration
import net.timafe.letsgo2.domain.User
import net.timafe.letsgo2.service.dto.UserDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private const val DEFAULT_LOGIN = "johndoe"
private const val DEFAULT_ID = "id1"

/**
 * Integration tests for [UserMapper].
 */
@SpringBootTest(classes = [Letsgo2App::class, TestSecurityConfiguration::class])
class UserMapperIT {

    @Autowired
    private lateinit var userMapper: UserMapper

    private lateinit var user: User
    private lateinit var userDto: UserDTO

    @BeforeEach
    fun init() {
        user = User(
            login = DEFAULT_LOGIN,
            activated = true,
            email = "johndoe@localhost",
            firstName = "john",
            lastName = "doe",
            imageUrl = "image_url",
            langKey = "en"
        )

        userDto = UserDTO(user)
    }

    @Test
    fun usersToUserDTOsShouldMapOnlyNonNullUsers() {
        val users = listOf(user, null)

        val userDTOS = userMapper.usersToUserDTOs(users)

        assertThat(userDTOS).isNotEmpty
        assertThat(userDTOS).size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersShouldMapOnlyNonNullUsers() {
        val usersDto = mutableListOf(userDto, null)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
    }

    @Test
    fun userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
        userDto.authorities = setOf("ADMIN")

        val usersDto = listOf(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isNotEmpty
        assertThat(users[0].authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOsToUsersMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = null

        val usersDto = listOf(userDto)

        val users = userMapper.userDTOsToUsers(usersDto)

        assertThat(users).isNotEmpty
        assertThat(users).size().isEqualTo(1)
        assertThat(users[0].authorities).isNotNull
        assertThat(users[0].authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithAuthoritiesStringShouldReturnUserWithAuthorities() {
        userDto.authorities = setOf("ADMIN")

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isNotEmpty
        assertThat(user.authorities.first().name).isEqualTo("ADMIN")
    }

    @Test
    fun userDTOToUserMapWithNullAuthoritiesStringShouldReturnUserWithEmptyAuthorities() {
        userDto.authorities = null

        val user = userMapper.userDTOToUser(userDto)

        assertNotNull(user)
        assertThat(user.authorities).isNotNull
        assertThat(user.authorities).isEmpty()
    }

    @Test
    fun userDTOToUserMapWithNullUserShouldReturnNull() {
        assertNull(userMapper.userDTOToUser(null))
    }

    @Test
    fun testUserFromId() {
        assertThat(userMapper.userFromId(DEFAULT_ID)?.id).isEqualTo(DEFAULT_ID)
        assertNull(userMapper.userFromId(null))
    }
}
