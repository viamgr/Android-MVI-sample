package com.example.composeempty.base

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeempty.pattern.*
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseViewModel<STATE : State, INTENT : Intent, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    val reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>,
    savedStateHandle: SavedStateHandle? = null
) : ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {


    val state: STATE get() = container.stateFlow.value

    protected abstract fun initialState(): STATE

    protected abstract suspend fun handleIntent(intent: INTENT): Flow<Result<EFFECT>>

    override val container =
        if (savedStateHandle != null)
            container<STATE, SIDE_EFFECT>(
                initialState = this.initialState(),
                savedStateHandle = savedStateHandle
            )
        else
            container(
                initialState = this.initialState()
            )

    open suspend fun onFailure(failure: Throwable) {

    }

    suspend fun SimpleSyntax<STATE, SIDE_EFFECT>.reduce(result: ReduceResult<STATE, SIDE_EFFECT>) {
        if (result.sideEffect != null)
            postSideEffect(result.sideEffect)
        reduce {
            result.state
        }
    }

    protected suspend fun SimpleSyntax<STATE, SIDE_EFFECT>.onLoading(intent: INTENT) {
        val loadingCapability: LoadingReducer<STATE, SIDE_EFFECT, INTENT>? = reducer.castOrNull()
        loadingCapability?.let {
            reduce(it.onLoading(state, intent))
        }
    }

    fun dispatchIntent(intent: INTENT) = intent {
        onLoading(intent)
        handleIntent(intent)
            .let { effectResult: Flow<Result<EFFECT>> ->
                effectResult
                    .onEach {
                        if (it.isFail()) {
                            val failure = it.requireError()
                            onFailure(failure)
                            reduceFailure(intent, failure)
                        }
                    }
                    .filter {
                        it.isSuccess()
                    }
                    .map {
                        it.requireData()
                    }
            }
            .let {
                handleEffects(it)
            }
    }

    private suspend fun SimpleSyntax<STATE, SIDE_EFFECT>.reduceFailure(
        intent: INTENT,
        failure: Throwable
    ) {
        val errorCapability: ErrorReducer<STATE, SIDE_EFFECT, INTENT>? = reducer.castOrNull()
        errorCapability?.let {
            reduce(it.onError(state, intent, failure))
        }
    }

    protected fun SimpleSyntax<STATE, SIDE_EFFECT>.handleEffects(effects: Flow<EFFECT>) =
        effects
            .map { effect ->
                reducer.reduce(state, effect)
            }
            .onEach { result ->
                reduce(result)
            }
            .catch { error ->
                reporter(error)
            }
            .launchIn(viewModelScope)

    protected fun dispatchEffects(effects: Flow<EFFECT>) = intent {
        handleEffects(effects)
    }

    open fun reporter(error: Throwable) {
        if (org.orbitmvi.orbit.viewmodel.BuildConfig.DEBUG) Log.e("reporter", "$error")
    }

}