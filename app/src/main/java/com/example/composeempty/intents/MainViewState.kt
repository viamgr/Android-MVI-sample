package com.example.composeempty.intents

import com.example.composeempty.pattern.State
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainViewState(
    val claps: Int = 0,
    val loading: Boolean = false,
    val error: Throwable? = null,
) : State