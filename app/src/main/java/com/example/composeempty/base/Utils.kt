package com.example.composeempty.base

import androidx.compose.runtime.snapshots.SnapshotApplyResult
import kotlinx.coroutines.flow.flowOf
import com.example.composeempty.base.Result.Success
fun <T> T.toFlow() = flowOf(this)

inline fun <reified R> Any.castOrNull(): R? {
    return if (this is R)
        this
    else null
}

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
fun <START_VAL, MID_VAL, END_VAL> ((START_VAL) -> MID_VAL).compare(f: (MID_VAL) -> END_VAL): (
    START_VAL
) -> END_VAL = {
    f(this(it))
}

/**
 * Success-biased flatMap() FP convention which means that Success is assumed to be the default case
 * to operate on. If it is Fail, operations like map, flatMap, ... return the Fail value unchanged.
 */
fun <T, R> Result<R>.flatMap(fn: (R) -> Result<T>): Result<T> =
    when (this) {
        is Result.Fail -> Result.Fail(error)
        is Success -> fn(this.invoke())
        is Result.Loading -> Result.Loading
    }

fun <T, R> Result<R>.map(fn: (R) -> (T)): Result<T> = this.flatMap(fn.compare { Success(it) })

/**
 * Composes 2 functions
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
suspend fun <START_VAL, MID_VAL, END_VAL> (suspend (START_VAL) -> MID_VAL).compare(
    f: (MID_VAL) -> END_VAL
): suspend (
    START_VAL
) -> END_VAL = {
    f(this(it))
}

/**
 * Suspend flatmap equivalent to flatmap but works in coroutines. Can't be name flatmap because of ambiguity issues in kotlin
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
suspend fun <T, R> Result<R>.suspendFlatMap(fn: suspend (R) -> Result<T>): Result<T> =
    when (this) {
        is Result.Fail -> Result.Fail(error)
        is Success -> fn(invoke())
        is Result.Loading -> Result.Loading
    }

/**
 * Suspend flatmap equivalent to map but works in coroutines. Can't be name flatmap because of ambiguity issues in kotlin
 * See <a href="https://proandroiddev.com/kotlins-nothing-type-946de7d464fb">Credits to Alex Hart.</a>
 */
suspend fun <T, R> Result<R>.suspendMap(fn: suspend (R) -> (T)) = this.suspendFlatMap(fn.compare { Success(it) })

/** Returns the value from this `Success` or the given argument if this is a `Fail`.
 *  Success(12).getOrElse(17) RETURNS 12 and Fail(12).getOrElse(17) RETURNS 17
 */
fun <R> Result<R>.dataOr(value: R): R =
    when (this) {
        is Success -> invoke()
        else -> value
    }

/** Returns the value from this `Success` or null if this is a `Fail`.
 *  Success(12).getOrNull() RETURNS 12 and Fail(12).getOrNull() RETURNS null
 */
fun <R> Result<R>.dataOrNull(): R? =
    when (this) {
        is Success -> invoke()
        else -> null
    }

/** Returns the value from this `Success` or null if this is a `Fail`.
 *  Success(12).getOrNull() RETURNS 12 and Fail(12).getOrNull() RETURNS null
 */
fun <R> Result<R>.failureOrNull(): SnapshotApplyResult.Failure? =
    when (this) {
        is Result.Fail -> this.error as SnapshotApplyResult.Failure
        else -> null
    }

/** Returns the value from this `Success` or throws a ClassCastException if this is a `Fail`.
 * @throws ClassCastException
 */
fun <R> Result<R>.requireData(): R =
    (this as Success<R>).invoke()

/** Returns the value from this `Fail` or throws a ClassCastException if this is a `Success`.
 * @throws ClassCastException
 */
fun <R> Result<R>.requireError(): Throwable =
    (this as Result.Fail).error

/**
 * Fail-biased onFailure() FP convention dictates that when this class is Fail, it'll perform
 * the onFailure functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <R> Result<R>.onFailure(fn: (failure: Throwable) -> Unit): Result<R> =
    this.apply { if (this is Result.Fail) fn(error) }

/**
 * Success-biased onSuccess() FP convention dictates that when this class is Success, it'll perform
 * the onSuccess functionality passed as a parameter, but, overall will still return an either
 * object so you chain calls.
 */
fun <R> Result<R>.onSuccess(fn: (success: R) -> Unit): Result<R> =
    this.apply { if (this is Success) fn(invoke()) }

/**
 * Returns true if this is a Success, false otherwise.
 * @see Success
 */
fun Result<*>.isSuccess(): Boolean = this is Success

/**
 * Returns true if this is a Fail, false otherwise.
 * @see Fail
 */
fun Result<*>.isFail(): Boolean = this is Result.Fail