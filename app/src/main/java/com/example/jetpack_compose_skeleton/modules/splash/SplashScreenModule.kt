package com.example.jetpack_compose_skeleton.modules.splash

import com.example.jetpack_compose_skeleton.AppTransition
import com.example.jetpack_compose_skeleton.modules.splash.ui.SplashScreenView
import com.example.jetpack_compose_skeleton.utils.navigator.composableScreen
import com.example.jetpack_compose_skeleton.utils.router.AppRouter
import com.example.jetpack_compose_skeleton.utils.router.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenModule<R : AppRouter>(
    private val router: R
) : Module {

    private var view = composableScreen("splashScreen") { SplashScreenView() }

    override fun start() {

        router.navigator.push(view, animated = false)

        CoroutineScope(Dispatchers.Main).launch {

            // dismiss splash after two seconds
            delay(2000)
            router.reset(AppTransition.Home)
        }
    }
}