package com.example.jetpack_compose_skeleton.utils.navigator

import androidx.compose.runtime.Composable

typealias AnyComposable = @Composable () -> Unit

class ComposableScreen(
    var name: String,
    var view: AnyComposable
)

fun composableScreen(name: String, composable: AnyComposable): ComposableScreen {

    return ComposableScreen(name = name, view = composable)
}