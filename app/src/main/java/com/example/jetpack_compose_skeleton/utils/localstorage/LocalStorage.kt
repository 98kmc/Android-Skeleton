@file:Suppress("unused")
package com.example.jetpack_compose_skeleton.utils.localstorage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * The `LocalStorage` class provides an abstraction for working with Android Jetpack Preferences DataStore.
 * It simplifies the storage and retrieval of datastore-supported primitive data types
 * (string, int, double, float, long, and string set), while providing default values when necessary.
 *
 * @property context The Android Context used for Preferences DataStore access.
 * @constructor Creates a `LocalStorage` instance with the given Android `context`.
 *
 *
 * Usage:
 *
 * // Initializing the LocalStorage
 *```
 * initLocalStorage(this)  // 'this' refers to the Android Context
 *```
 *
 * // Storing a string value by providing a new value
 *```
 * val data1: String = stringLocalStorage(LocalStorageKey.Key1, "DefaultValue")
 *```
 *
 * // Retrieving a string value. (If no value is found for the key, it will return a default value)
 *```
 * val data2: String = stringLocalStorage(LocalStorageKey.Key1)
 *```
 * // Storing an integer value by providing a new value
 * ```
 * val data3: Int = intLocalStorage(LocalStorageKey.Key3, 42)
 *```
 * // Retrieving an integer value
 *```
 *  val data4: Int = intLocalStorage(LocalStorageKey.Key4) // (If no value is found for the key, it will return a default value)
 *```
 *
 * // Similar methods are available for double, long, float, and string set preferences.
 *
 * @see stringLocalStorage
 * @see intLocalStorage
 * @see doubleLocalStorage
 * @see longLocalStorage
 * @see floatLocalStorage
 * @see stringSetLocalStorage
 */

private class LocalStorage(
    private val context: Context
) {

    /**
     *
     * This extension property allows easy access to the Preferences DataStore for storing and retrieving data.
     *
     * Usage:
     * ```
     * val context: Context = // Your Android Context
     * val dataStore: DataStore<Preferences> = context.dataStore
     * ```
     *
     * @property Context.dataStore: DataStore<Preferences> by preferencesDataStore
     * @receiver The Android Context for which the DataStore is accessed.
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = LocalStorageKey.LOCAL_STORAGE_PATH.value
    )

    /**
     * Saves a value of type `T` with the specified key in the Preferences DataStore.
     *
     * @param value The value to be saved.
     * @param withKey The key associated with the preference.
     * @return The saved value.
     */
    private suspend fun <T> saveValue(value: T, withKey: Preferences.Key<T>): T {

        context.dataStore.edit { preferences ->
            preferences[withKey] = value
        }

        return value
    }

    /**
     * Retrieves a value of type `T` from the Preferences DataStore using the specified key.
     *
     * @param preferenceKey The key associated with the preference.
     * @return The stored value associated with the given key, or null if not found.
     */
    private suspend fun <T> getValue(preferenceKey: Preferences.Key<T>): T? {
        return context.dataStore.data.first()[preferenceKey]
    }

    /**
     * A utility function that either retrieves a value using `getValue` or saves a new value using `saveValue`
     * based on whether a new value is provided or not.
     *
     * @param preferenceKey The key associated with the preference.
     * @param newValue The new value to set for the preference.
     * @param default The default value to save if the preference is not found.
     * @return The stored value associated with the given key or the default value if not found.
     */
    fun <T> getOrPut(preferenceKey: Preferences.Key<T>, newValue: T?, default: T): T {

        return runBlocking {
            if (newValue != null) {
                saveValue(newValue, preferenceKey)
            } else {
                getValue(preferenceKey) ?: saveValue(default, preferenceKey)
            }
        }
    }

    /**
     * Removes a specific preference associated with the provided key from the Preferences DataStore.
     *
     * @param key The unique identifier for the preference to be removed.
     * @return `true` if the operation was successful, `false` otherwise.
     */
    suspend fun <T> removePreference(key: Preferences.Key<T>): Boolean {
        return try {
            context.dataStore.edit { it.remove(key) }
            true
        } catch (error: Exception) {
            Log.d("LocalStorage", "removePreferenceError: $error")
            false
        }
    }

    /**
     * Clears all preferences stored in the Preferences DataStore.
     *
     * This function removes all preferences, effectively resetting the DataStore.
     *
     * @return `true` if the operation was successful, `false` otherwise.
     */
    suspend fun clearAllPreferences(): Boolean {

        return try {
            context.dataStore.edit { it.clear() }
            true
        } catch (error: Exception) {
            Log.d("LocalStorage", "clearAllPreferenceError: $error")
            false
        }
    }
}

private class LocalStorageManager {

    var isInitialized: Boolean = false

    lateinit var ls: LocalStorage

    fun init(context: Context) {
        ls = LocalStorage(context)
        isInitialized = true
    }
}

private val manager: LocalStorageManager = LocalStorageManager()

private fun verifyInit() {

    if (!manager.isInitialized) throw NullPointerException(
        "LocalStorage instance has not been initialized in this context" +
                "Call initLocalStorage() before reading or writing any value."
    )
}
fun initLocalStorage(withContext: Context) {
    manager.init(withContext)
}

/**
 * Retrieves or stores a boolean value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be 'false').
 * @return The stored or default boolean value associated with the given key.
 */
fun booleanLocalStorage(key: LocalStorageKey, newValue: Boolean? = null): Boolean {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = booleanPreferencesKey(key.value), newValue = newValue, default = false
    )
}

/**
 * Retrieves or stores a string value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be an empty string).
 * @return The stored or default string value associated with the given key.
 */
fun stringLocalStorage(key: LocalStorageKey, newValue: String? = null): String {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = stringPreferencesKey(key.value),
        newValue = newValue,
        default = ""
    )
}

/**
 * Retrieves or stores an Int value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be '0').
 * @return The stored or default Int value associated with the given key.
 */
fun intLocalStorage(key: LocalStorageKey, newValue: Int? = null): Int {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = intPreferencesKey(key.value),
        newValue = newValue,
        default = 0
    )
}

/**
 * Retrieves or stores a Double value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be '0.0').
 * @return The stored or default Double value associated with the given key.
 */
fun doubleLocalStorage(key: LocalStorageKey, newValue: Double? = null): Double {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = doublePreferencesKey(key.value),
        newValue = newValue,
        default = 0.0
    )
}

/**
 * Retrieves or stores a Long value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be '0L').
 * @return The stored or default Long value associated with the given key.
 */
fun longLocalStorage(key: LocalStorageKey, newValue: Long? = null): Long {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = longPreferencesKey(key.value),
        newValue = newValue,
        default = 0L
    )
}

/**
 * Retrieves or stores a Float value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be '0f').
 * @return The stored or default Float value associated with the given key.
 */
fun floatLocalStorage(key: LocalStorageKey, newValue: Float? = null): Float {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = floatPreferencesKey(key.value),
        newValue = newValue,
        default = 0f
    )
}

/**
 * Retrieves or stores a Set<String> value in the Preferences DataStore.
 *
 * @param key The unique identifier for the preference.
 * @param newValue The optional new value to set for the preference
 *                 (if set to null, the default saved value will be an empty String Set 'setOf<String>()' ).
 * @return The stored or default Set<String> value associated with the given key.
 */
fun stringSetLocalStorage(key: LocalStorageKey, newValue: Set<String>? = null): Set<String> {

    verifyInit()

    return manager.ls.getOrPut(
        preferenceKey = stringSetPreferencesKey(key.value),
        newValue = newValue,
        default = setOf()
    )
}

suspend fun <T> removeLocalStoragePreference(preference: Preferences.Key<T>): Boolean {

    verifyInit()
    return manager.ls.removePreference(preference)
}

suspend fun clearAllLocalStoragePreferences(): Boolean {

    verifyInit()
    return manager.ls.clearAllPreferences()
}