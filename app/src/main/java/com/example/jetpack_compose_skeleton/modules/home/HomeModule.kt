package com.example.jetpack_compose_skeleton.modules.home

import androidx.activity.compose.BackHandler
import com.example.jetpack_compose_skeleton.modules.home.ui.HomeView
import com.example.jetpack_compose_skeleton.utils.navigator.composableScreen
import com.example.jetpack_compose_skeleton.utils.router.AppRouter
import com.example.jetpack_compose_skeleton.utils.router.Module

class HomeModule<R : AppRouter>(
    private val router: R
) : Module {

    override fun start() {

        val view = composableScreen("Home_screen") {

            BackHandler { router.pop() }
            HomeView()
        }

        router.navigator.push(view)
    }
}