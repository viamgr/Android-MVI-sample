package com.example.composeempty.pattern

abstract class Reducer<STATE : State, EFFECT : Effect, SIDE_EFFECT : SideEffect> {
    abstract suspend fun reduce(state: STATE, effect: EFFECT): ReduceResult<STATE, SIDE_EFFECT>
}

interface LoadingReducer<STATE : State, SIDE_EFFECT : SideEffect, INTENT : Intent> {
    suspend fun onLoading(state: STATE, intent: INTENT): ReduceResult<STATE, SIDE_EFFECT>
}

interface ErrorReducer<STATE : State, SIDE_EFFECT : SideEffect, INTENT : Intent> {
    suspend fun onError(state: STATE, intent: INTENT, failure: Throwable): ReduceResult<STATE, SIDE_EFFECT>
}

class ReduceResult<STATE : State, SIDE_EFFECT : SideEffect>(
    val state: STATE,
    val sideEffect: SIDE_EFFECT? = null
)