package com.example.jetpack_compose_skeleton.utils.router

import com.example.jetpack_compose_skeleton.AppTransition
import com.example.jetpack_compose_skeleton.utils.navigator.Navigator

interface Router<Route> {

    fun process(route: Route, animated: Boolean = false)
}

interface AppRouter : Router<AppTransition> {

    val navigator: Navigator

    fun pop(animated: Boolean = false)

    fun popToRoot(animated: Boolean = false)

    fun reset(startDestination: AppTransition, animated: Boolean = false)
}