package com.taoufikcode.core.data.dto

import com.taoufikcode.core.domain.auth.AuthInfo
import com.taoufikcode.core.domain.auth.User

fun AuthInfoDto.toDomain(): AuthInfo {
    return AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDomain()
    )
}

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        userName = username,
        hasVerifiedEmail = hasVerifiedEmail,
        profilePictureUrl = profilePictureUrl
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = userName,
        hasVerifiedEmail = hasVerifiedEmail,
        profilePictureUrl = profilePictureUrl
    )
}

fun AuthInfo.toDto(): AuthInfoDto {
    return AuthInfoDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toDto()
    )
}