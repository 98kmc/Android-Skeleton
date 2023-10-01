package com.example.jetpack_compose_skeleton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.example.jetpack_compose_skeleton.ui.theme.JetpackComposeSkeletonTheme

class MainActivity : ComponentActivity() {

    private lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeSkeletonTheme {

                app = this.application as App
                app.navigator.Host {
                    app.didStart = false
                    app.startModule = AppTransition.Splash
                    finish()
                }

                LaunchedEffect(key1 = true) {
                    app.start()
                }
            }
        }
    }
}
