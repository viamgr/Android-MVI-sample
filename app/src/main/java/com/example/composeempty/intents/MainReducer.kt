package com.example.composeempty.intents

import com.example.composeempty.pattern.ErrorReducer
import com.example.composeempty.pattern.LoadingReducer
import com.example.composeempty.pattern.ReduceResult
import com.example.composeempty.pattern.Reducer

class MainReducer : Reducer<MainViewState, MainEffect, MainSideEffect>(),
    ErrorReducer<MainViewState, MainSideEffect, MainIntent>,
    LoadingReducer<MainViewState, MainSideEffect, MainIntent> {

    override suspend fun reduce(
        state: MainViewState,
        effect: MainEffect
    ): ReduceResult<MainViewState, MainSideEffect> {
        return when (effect) {
            is MainEffect.Claps ->
                ReduceResult(
                    state = state.copy(claps = effect.count, error = null, loading = false),
                )
            MainEffect.Toast ->
                ReduceResult(
                    state = state,
                    sideEffect = MainSideEffect.Toast
                )
        }
    }

    override suspend fun onError(
        state: MainViewState,
        intent: MainIntent,
        failure: Throwable
    ): ReduceResult<MainViewState, MainSideEffect> {
        return ReduceResult(
            state = state.copy(claps = 0, error = failure, loading = false)
        )
    }

    override suspend fun onLoading(
        state: MainViewState,
        intent: MainIntent
    ): ReduceResult<MainViewState, MainSideEffect> {
        return ReduceResult(
            state = state.copy(error = null, loading = true)
        )
    }
}