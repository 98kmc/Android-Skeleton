package com.example.jetpack_compose_skeleton

import com.example.jetpack_compose_skeleton.modules.home.HomeModule
import com.example.jetpack_compose_skeleton.modules.splash.SplashScreenModule
import com.example.jetpack_compose_skeleton.utils.router.AppRouter
import com.example.jetpack_compose_skeleton.utils.router.Module

sealed class AppTransition {

    object Splash : AppTransition()
    object Home : AppTransition()

    fun moduleFor(router: AppRouter): Module {

        return when (this) {
            is Splash -> SplashScreenModule(router)
            is Home -> HomeModule(router)
        }
    }
}
