package com.example.composeempty.intents

import com.example.composeempty.pattern.Effect

sealed class MainEffect : Effect {
    data class Claps(val count: Int) : MainEffect()
    object Toast : MainEffect()
}