package com.example.composeempty.intents

import com.example.composeempty.pattern.Intent

sealed class MainIntent : Intent {
    object ClapsClicked : MainIntent()
}