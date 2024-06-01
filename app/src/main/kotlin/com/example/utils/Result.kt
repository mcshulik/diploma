package com.example.utils

import java.util.Optional

/**
 *@since 01/06/2024
 *@author Paval Shlyk
 */
sealed interface Result<out T> {
    fun isOk(): Boolean
    fun isErr(): Boolean
    fun asErr(): Optional<Err> {
        if (isErr()) {
            return Optional.of(this as Err)
        }
        return Optional.empty();
    }
}

data class Ok<out T>(val value: T) : Result<T> {
    override fun isOk(): Boolean = true;

    override fun isErr(): Boolean = false;
}

data class Err(val message: String) : Result<Nothing> {
    override fun isOk(): Boolean = false;

    override fun isErr(): Boolean = false;

}

object Results {
    @JvmStatic
    fun ok(): Result<Unit> = Ok(Unit)

    @JvmStatic
    fun <T> ok(value: T): Result<T> = Ok(value)

    @JvmStatic
    fun withCause(message: String): Result<Nothing> = Err(message)
}