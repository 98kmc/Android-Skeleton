package com.example.jetpack_compose_skeleton

import android.app.Application
import com.example.jetpack_compose_skeleton.utils.localstorage.initLocalStorage
import com.example.jetpack_compose_skeleton.utils.navigator.AppNavigator
import com.example.jetpack_compose_skeleton.utils.router.AppRouter
import com.example.jetpack_compose_skeleton.utils.router.Module
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), AppRouter, Module {

    @Inject
    override lateinit var navigator: AppNavigator

    var didStart: Boolean = false
    var startModule: AppTransition = AppTransition.Splash

    override fun start() {

        if (!didStart) {

            initLocalStorage(this)
            didStart = true
            process(startModule)
        }
    }

    // AppRouter
    //==================================================================
    override fun pop(animated: Boolean) {
        navigator.pop(animated = animated)
    }

    override fun popToRoot(animated: Boolean) {

        navigator.pop(navigator.backStack.size - 1, animated = animated)
    }

    override fun reset(startDestination: AppTransition, animated: Boolean) {

        navigator.clearBackStack()
        process(startDestination)
        this.startModule = startDestination
    }

    override fun process(route: AppTransition, animated: Boolean) {

        val module = route.moduleFor(this)
        module.start()
    }
}

