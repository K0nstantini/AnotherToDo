package com.homemade.anothertodo.utils.delegates

import android.content.SharedPreferences
import com.homemade.anothertodo.add_classes.MyCalendar
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class PreferencesDelegate<TValue>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defValue: TValue
) : ReadWriteProperty<Any?, TValue> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): TValue {
        with(preferences) {
            return when (defValue) {
                is Boolean -> (getBoolean(name, defValue) as? TValue) ?: defValue
                is Int -> (getInt(name, defValue) as TValue) ?: defValue
                is Float -> (getFloat(name, defValue) as TValue) ?: defValue
                is Long -> (getLong(name, defValue) as TValue) ?: defValue
                is String -> (getString(name, defValue) as TValue) ?: defValue
                is MyCalendar -> (MyCalendar(getLong(name, defValue.milli)) as TValue) ?: defValue
                else -> throw NotFoundImplementationException(defValue)
            }
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: TValue) {
        with(preferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(name, value)
                is Int -> putInt(name, value)
                is Float -> putFloat(name, value)
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                else -> throw NotFoundImplementationException(value)
            }
            apply()
        }
    }

    class NotFoundImplementationException(defValue: Any?) :
        Exception("not found implementation for $defValue")
}