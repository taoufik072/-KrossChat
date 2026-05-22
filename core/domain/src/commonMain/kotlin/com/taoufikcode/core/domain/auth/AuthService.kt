package com.taoufikcode.core.domain.auth

import com.taoufikcode.core.domain.util.DataError
import com.taoufikcode.core.domain.util.EmptyResult

interface AuthService {
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<DataError.Remote>
}