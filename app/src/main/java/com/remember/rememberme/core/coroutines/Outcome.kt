package com.remember.rememberme.core.coroutines

sealed class Outcome<Success> {

    abstract val isSuccess: Boolean

    val isFailure: Boolean
        get() = !isSuccess

    data class Success<Success>(val value: Success) : Outcome<Success>() {
        override val isSuccess: Boolean
            get() = true
    }

    data class Error<Success>(val value: Exception) : Outcome<Success>() {
        override val isSuccess: Boolean
            get() = false
    }

    inline fun onSuccess(toExecute: (Success) -> Unit): Outcome<Success> {
        if (this is Outcome.Success) {
            toExecute(this.value)
        }

        return this
    }

    inline fun onError(toExecute: (Exception) -> Unit): Outcome<Success> {
        if (this is Error) {
            toExecute(this.value)
        }

        return this
    }

    fun <NewSuccess> map(transform: (Success) -> NewSuccess): Outcome<NewSuccess> {
        return when (this) {
            is Outcome.Success -> Success(transform(value))
            is Error -> Error(value)
        }
    }

    fun mapToUnit(): Outcome<Unit> {
        return when (this) {
            is Outcome.Success -> Success(Unit)
            is Error -> Error(value)
        }
    }

    suspend fun <NewSuccess> then(
        toExecute: suspend (Success) -> Outcome<NewSuccess>
    ): Outcome<NewSuccess> {
        return when (this) {
            is Outcome.Success -> toExecute(this.value)
            is Error -> Error(value)
        }
    }

    fun successOrNull(): Success? {
        return when (this) {
            is Outcome.Success -> this.value
            is Error -> null
        }
    }

    fun errorOrNull(): Exception? {
        return when (this) {
            is Error -> this.value
            is Outcome.Success -> null
        }
    }
}
