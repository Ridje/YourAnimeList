package com.kis.youranimelist.domain.model

import android.accounts.NetworkErrorException
import com.haroldadmin.cnradapter.NetworkResponse

sealed class ResultWrapper<out T> {
    data class Success<T>(val data: T) : ResultWrapper<T>()
    data class Error(val throwable: Throwable) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}

fun <I, O> NetworkResponse<I, *>.asResult(
    mapper: (I) -> O,
): ResultWrapper<O> {
    return when (this) {
        is NetworkResponse.Success -> ResultWrapper.Success(mapper.invoke(this.body))
        is NetworkResponse.ServerError -> ResultWrapper.Error(this.error ?: NetworkErrorException(this.response?.message()))
        is NetworkResponse.NetworkError -> ResultWrapper.Error(this.error)
        is NetworkResponse.UnknownError -> ResultWrapper.Error(this.error)
    }
}

fun <I> NetworkResponse<I, *>.asResult(
): ResultWrapper<I> {
    return when (this) {
        is NetworkResponse.Success -> ResultWrapper.Success(this.body)
        is NetworkResponse.ServerError -> ResultWrapper.Error(this.error ?: NetworkErrorException(this.response?.message()))
        is NetworkResponse.NetworkError -> ResultWrapper.Error(this.error)
        is NetworkResponse.UnknownError -> ResultWrapper.Error(this.error)
    }
}
