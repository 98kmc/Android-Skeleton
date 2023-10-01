@file:Suppress("unused")
package com.example.jetpack_compose_skeleton.utils.navigator

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AppNavigator is a class responsible for managing the navigation between
 * Composable screens using Jetpack Compose and the Navigation component.
 * It supports pushing and popping screens with optional animations.
 *
 * @constructor Creates an instance of AppNavigator.
 */
class AppNavigator @Inject constructor() : Navigator {

    /**
     * Represents the possible states of the AppNavigator.
     */
    sealed class State {

        object Running : State() // The navigator is running normally.
        object Pushing : State() // A screen push operation is in progress.
        object Popping : State() // A screen pop operation is in progress.
    }

    private var _backStack = mutableListOf<ComposableScreen>()
    private var transitionJob: Job? = null
    private var _currentState: State = State.Running
    private var _animated: Boolean = false

    lateinit var finish: () -> Unit

    /**
     * Gets the current back stack of Composable screens.
     */
    val backStack get() = _backStack

    /**
     * Gets the current state of the navigator.
     */
    val state get() = _currentState

    /**
     * Gets the current animation status.
     */
    val animated get() = _animated

    private enum class ScreenContainer {
        CURRENT,
        NEXT
    }

    private lateinit var navController: NavHostController
    private var currentScreenContainer: ScreenContainer = ScreenContainer.CURRENT
    private var currentScreen: AnyComposable = { }
    private var nextScreen: AnyComposable = { }

    private val transitions: NavigatorTransitionsProvider = NavigatorTransitionsProvider(this)

    /**
     * Sets up the navigation host for the AppNavigator.
     *
     * @param onFinish A callback to be invoked when navigation finishes.
     */
    @Composable
    fun Host(onFinish: () -> Unit) {

        navController = rememberNavController()
        finish = onFinish

        NavHost(
            navController = navController,
            startDestination = ScreenContainer.CURRENT.name
        ) {

            composable(
                ScreenContainer.CURRENT.name,
                enterTransition = { transitions.pushEnterTransition() },
                popEnterTransition = { transitions.popEnterTransition() },
                popExitTransition = { transitions.popExitTransition() }

            ) {

                BackHandler { }
                currentScreen()
            }

            composable(
                ScreenContainer.NEXT.name,
                enterTransition = { transitions.pushEnterTransition() },
                popEnterTransition = { transitions.popEnterTransition() },
                popExitTransition = { transitions.popExitTransition() }
            ) {

                BackHandler { }
                nextScreen()
            }
        }
    }


    // Navigation
    //==================================================================

    /**
     * Pops a specified number of screens from the navigation stack.
     *
     * This method allows you to remove screens from the navigation stack,
     * with the option to enable animations during the transition.
     *
     * @param count The number of screens to pop from the navigation stack.
     * @param animated A boolean indicating whether to use animations during the screen transition.
     */
    override fun pop(count: Int, animated: Boolean) {

        if (_currentState != State.Running) return

        _currentState = State.Popping
        this._animated = animated

        navigateBack(count)
    }

    /**
     * Pops screens from the navigation stack until a specific route is reached.
     *
     * @param screenName The route to which screens should be popped.
     * @param animated A boolean indicating whether to use animations during the screen transition.
     */
    override fun popTo(screenName: String, animated: Boolean) {

        if (_currentState != State.Running) return

        _currentState = State.Popping
        this._animated = animated

        val index = _backStack.indexOfFirst { it.name == screenName }

        if (index == -1) {

            // The specified screen was not found in the back stack.
            _currentState = State.Running
            return
        }

        val screensToPop = _backStack.size - index - 1

        if (screensToPop <= 0) {

            // No screens to pop! the specified screen is already at the top of the stack.
            _currentState = State.Running
            return
        }

        navigateBack(screensToPop)
    }

    /**
     * Pushes a new Composable screen onto the navigation stack.
     *
     * This method allows you to add a new screen to the navigation stack,
     * with the option to enable animations during the transition.
     *
     * @param screen The ComposableScreen to push onto the stack.
     * @param animated A boolean indicating whether to use animations during the screen transition.
     */
    override fun push(screen: ComposableScreen, animated: Boolean) {

        if (_currentState != State.Running) return

        _currentState = State.Pushing
        this._animated = animated

        if (currentScreenContainer == ScreenContainer.CURRENT) {
            currentScreen = screen.view
        } else {
            nextScreen = screen.view
        }

        navController.navigate(currentScreenContainer.name)
        _backStack.add(screen)
        endTransition()
    }

    /**
     * Clears the entire back stack of Composable screens.
     *
     * This method removes all screens from the back stack, effectively emptying it.
     */
    fun clearBackStack() {
        _backStack.clear()
    }

    /**
     * Pops a specified number of screens from the navigation stack and returns the last popped screen.
     *
     * @param count The number of screens to pop from the navigation stack.
     * @return The last screen popped from the stack, or null if the stack becomes empty.
     */
    private fun popBackStack(count: Int): ComposableScreen? {

        for (i in 1..count) {
            _backStack.removeLast()
            if (_backStack.isEmpty()) return null
        }
        return _backStack.last()
    }


    /**
     * Navigates back to a specific screen within the navigation stack.
     *
     * This method determines which screen container is currently active ('current' or 'next'),
     * updates the current or next screen accordingly, and navigates back to the previous screen.
     *
     * @param count The ComposableScreen to navigate back to.
     */
    private fun navigateBack(count: Int) {

        val previousScreen = popBackStack(count) ?: run {
            _currentState = State.Running
            clearBackStack()
            finish()
            return
        }

        // Determines which screen/module container is currently active ('current' or 'next').
        // Updates the current or next screen accordingly.
        if (currentScreenContainer == ScreenContainer.CURRENT) {
            currentScreen = previousScreen.view
        } else {
            nextScreen = previousScreen.view
        }

        navController.popBackStack(currentScreenContainer.name, false)
        endTransition()
    }

    /**
     * Ends the transition after a delay.
     *
     * This method cancels any ongoing transition job, schedules a delay,
     * and toggles between 'current' and 'next' containers to prepare for the next screen.
     */
    private fun endTransition() {

        // If another transition is initiated before the current
        // coroutine completes, the job is canceled and restarted.
        transitionJob?.cancel()

        transitionJob = CoroutineScope(Dispatchers.IO).launch {
            delay(transitions.afterTransitionDelayTime)

            // Toggles between 'current' and 'next' containers to prepare for the next screen.
            currentScreenContainer = if (currentScreenContainer == ScreenContainer.CURRENT) {
                ScreenContainer.NEXT
            } else {
                ScreenContainer.CURRENT
            }
            _animated = false
            _currentState = State.Running
        }
    }
}

/**
 * The `NavigatorTransitionsProvider` class is responsible for defining navigation transitions used by the [AppNavigator] class.
 * It encapsulates the logic for creating transitions such as slide-in, slide-out, and fading animations.
 *
 * @property navigator The parent [AppNavigator] instance to which these transitions belong.
 * @property durationInMillis The duration of the transitions in milliseconds.
 */
private class NavigatorTransitionsProvider(
    private val navigator: AppNavigator,
    private val durationInMillis: Int = 300
) {

    /**
     * The delay time after transitions in milliseconds.
     */
    val afterTransitionDelayTime = (durationInMillis * 2.5).toLong()

    /**
     * Creates a slide-in transition.
     *
     * @param targetOffsetX A lambda function providing the offset value for the animation.
     * @return An [EnterTransition] representing the slide-in transition.
     */
    private fun slideInTransition(targetOffsetX: (Int) -> Int): EnterTransition {

        return if (navigator.animated && navigator.state is AppNavigator.State.Pushing) {
            slideInHorizontally(
                initialOffsetX = targetOffsetX,
                animationSpec = tween(durationInMillis)
            )
        } else {
            fadeIn()
        }
    }

    /**
     * Creates a slide-out transition.
     *
     * @param targetOffsetX A lambda function providing the offset value for the animation.
     * @return An [ExitTransition] representing the slide-out transition.
     */
    private fun slideOutTransition(targetOffsetX: (Int) -> Int): ExitTransition {

        return if (navigator.animated && navigator.state is AppNavigator.State.Popping) {

            slideOutHorizontally(
                targetOffsetX = targetOffsetX,
                animationSpec = tween(durationInMillis)
            )
        } else {
            fadeOut()
        }
    }

    /**
     * The enter transition used when pushing a screen/module.
     *
     * @return A lambda function providing the enter transition.
     */
    val pushEnterTransition: () -> EnterTransition = {

        slideInTransition { it }
    }

    /**
     * The exit transition used when pushing a screen/module.
     *
     * @return A lambda function providing the exit transition.
     */
    val pushExitTransition: () -> ExitTransition = {

        fadeOut()
    }

    /**
     * The enter transition used when popping a screen/module.
     *
     * @return A lambda function providing the enter transition.
     */
    val popEnterTransition: () -> EnterTransition = {

        slideInTransition { -it }
    }

    /**
     * The exit transition used when popping a screen/module.
     *
     * @return A lambda function providing the exit transition.
     */
    val popExitTransition: () -> ExitTransition = {

        slideOutTransition { it }
    }
}