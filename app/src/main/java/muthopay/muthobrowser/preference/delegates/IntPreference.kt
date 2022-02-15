package com.muthopay.muthobrowser.preference.delegates

import android.content.SharedPreferences
import androidx.annotation.StringRes
import com.muthopay.muthobrowser.BrowserApp
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * An [Int] delegate that is backed by [SharedPreferences].
 */
private class IntPreferenceDelegate(
    private val name: String,
    private val defaultValue: Int,
    private val preferences: SharedPreferences
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int =
        preferences.getInt(name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.edit().putInt(name, value).apply()
    }

}

/**
 * Creates a [Int] from [SharedPreferences] with the provide arguments.
 */
fun SharedPreferences.intPreference(
    name: String,
    defaultValue: Int
): ReadWriteProperty<Any, Int> = IntPreferenceDelegate(name, defaultValue, this)


/**
 * Creates a [Int] from [SharedPreferences] with the provide arguments.
 */
fun SharedPreferences.intPreference(
        @StringRes stringRes: Int,
        defaultValue: Int
): ReadWriteProperty<Any, Int> = IntPreferenceDelegate(BrowserApp.instance.resources.getString(stringRes), defaultValue, this)
