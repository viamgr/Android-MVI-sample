package com.example.composeempty.base

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Result] are either an instance of [Fail] or [Success].
 * FP Convention dictates that [Fail] is used for "failure"
 * and [Success] is used for "success".
 *
 * @see Fail
 * @see Success
 */
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Fail(val error: Throwable) : Result<Nothing>()
    data class Success<out T>(private val value: T) : Result<T>() {
        operator fun invoke(): T = value
    }
}

