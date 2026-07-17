package com.taoufikcode.core.data.dto

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.User
import kotlin.test.Test

class MappersTest {

    private val userDto = UserDto(
        id = "user-1",
        email = "user@example.com",
        username = "kross",
        hasVerifiedEmail = true,
        profilePictureUrl = "https://cdn.example.com/pic.png"
    )

    private val authInfoDto = AuthInfoDto(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        user = userDto
    )

    @Test
    fun `UserDto maps to domain with username renamed to userName`() {
        val user = userDto.toDomain()

        assertThat(user).isEqualTo(
            User(
                id = "user-1",
                email = "user@example.com",
                userName = "kross",
                hasVerifiedEmail = true,
                profilePictureUrl = "https://cdn.example.com/pic.png"
            )
        )
    }

    @Test
    fun `UserDto with null profile picture maps to null in domain`() {
        val user = userDto.copy(profilePictureUrl = null).toDomain()

        assertThat(user.profilePictureUrl).isNull()
    }

    @Test
    fun `AuthInfoDto maps tokens and nested user to domain`() {
        val authInfo = authInfoDto.toDomain()

        assertThat(authInfo).isEqualTo(
            AuthInfo(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                user = userDto.toDomain()
            )
        )
    }

    @Test
    fun `User round-trips through dto unchanged`() {
        val user = userDto.toDomain()

        assertThat(user.toDto().toDomain()).isEqualTo(user)
    }

    @Test
    fun `AuthInfo round-trips through dto unchanged`() {
        val authInfo = authInfoDto.toDomain()

        assertThat(authInfo.toDto().toDomain()).isEqualTo(authInfo)
    }

    @Test
    fun `AuthInfoDto round-trips through domain unchanged`() {
        assertThat(authInfoDto.toDomain().toDto()).isEqualTo(authInfoDto)
    }
}
