package com.example.jetpack_compose_skeleton.utils.navigator

interface Navigator {

    fun pop(count: Int = 1, animated: Boolean = false)

    fun popTo(screenName: String, animated: Boolean = false)

    fun push(screen: ComposableScreen, animated: Boolean = false)
}