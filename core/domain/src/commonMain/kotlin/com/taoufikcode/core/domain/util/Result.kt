package com.taoufikcode.core.domain.util

sealed interface Result<out S, out E : Error> {
    data class Success<out D_S>(val data: D_S) : Result<D_S, Nothing>
    data class Failure<out D_E : Error>(val error: D_E) : Result<Nothing, D_E>
}

inline fun <S, E : Error, D> Result<S, E>.map(map: (S) -> D): Result<D, E> {
    return when (this) {
        is Result.Failure -> Result.Failure(error)
        is Result.Success -> Result.Success(data = map(data))
    }
}

inline fun <S, E : Error> Result<S, E>.onSuccess(action: (S) -> Unit): Result<S, E> {
    return when (this) {
        is Result.Failure -> this
        is Result.Success -> {
            action(this.data)
            this
        }
    }
}
inline fun <S, E: Error> Result<S, E>.onFailure(action: (E) -> Unit): Result<S, E> {
    return when(this) {
        is Result.Failure -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

fun <S, E: Error> Result<S, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}

typealias EmptyResult<E> = Result<Unit, E>
