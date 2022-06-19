package com.example.composeempty.intents

import com.example.composeempty.pattern.SideEffect

sealed class MainSideEffect : SideEffect {
    object Toast : MainSideEffect()
}