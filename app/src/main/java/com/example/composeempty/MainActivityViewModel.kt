package com.example.composeempty

import android.util.Log
import com.example.composeempty.base.BaseViewModel
import com.example.composeempty.base.Result
import com.example.composeempty.intents.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

class MainActivityViewModel :
    BaseViewModel<MainViewState, MainIntent, MainEffect, MainSideEffect>(MainReducer()) {

    override suspend fun handleIntent(intent: MainIntent): Flow<Result<MainEffect>> {
        return when (intent) {
            is MainIntent.ClapsClicked -> handleClapsClicked()
        }
    }


    private suspend fun handleClapsClicked(): Flow<Result<MainEffect>> {
        delay(500L)
        return when {
            state.claps == 3 -> {
                flowOf(Result.Success(MainEffect.Toast))
            }
            Random.nextBoolean() -> {
                flowOf(Result.Success(MainEffect.Claps(state.claps + 1)))
            }
            else -> {
                flowOf(Result.Fail(Exception("A Random Exception")))
            }
        }
    }


    override suspend fun onFailure(failure: Throwable) {
        Log.e("onFailure", "$failure")
    }

    override fun initialState(): MainViewState = MainViewState()

}