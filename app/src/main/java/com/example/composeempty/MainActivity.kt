package com.example.composeempty

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeempty.intents.MainIntent
import com.example.composeempty.intents.MainSideEffect
import com.example.composeempty.ui.theme.ComposeEmptyTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()

            ComposeEmptyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: MainActivityViewModel = viewModel()
                    val state by viewModel.container.stateFlow.collectAsState()

                    SideEffect {
                        viewModel.container.sideEffectFlow
                            .onEach {
                                when (it) {
                                    is MainSideEffect.Toast -> toast()
                                }
                            }
                            .launchIn(coroutineScope)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Greeting("Max number of claps is: 3")

                        if (state.loading)
                            CircularProgressIndicator()

                        if (state.claps < 3)
                            Button(modifier = Modifier.padding(20.dp),
                                onClick = {
                                    viewModel.dispatchIntent(MainIntent.ClapsClicked)
                                }) {
                                Text("Claps : ${state.claps}")
                            }
                        else
                            Text("Congratulation!!!")

                        state.error?.message?.let {
                            Text(state.error.toString())
                        }
                    }
                }
            }
        }
    }


    private fun toast() {
        Toast.makeText(this, "Claps become 3", Toast.LENGTH_SHORT)
            .show()
    }
}


@Composable
fun Greeting(text: String) {
    Text(text = text)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeEmptyTheme {
        Greeting("Android")
    }
}