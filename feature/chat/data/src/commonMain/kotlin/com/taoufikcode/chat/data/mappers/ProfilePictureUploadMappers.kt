package com.taoufikcode.chat.data.mappers

import com.taoufikcode.chat.data.di.response.ProfilePictureUploadUrlsResponse
import com.taoufikcode.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}