package com.guidoroos.visproweather.util


import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val PREFS = "preferences"
    const val USE_FAHRENHEIT = "fahrenheit"
    const val LAST_LOCATIONS = "lastLocations"

    private fun getSharedPref(context:Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun saveToPreferences(keyName: String, stringSet: Set<String>, context:Context) {
        val editor = getSharedPref(context).edit()
        editor.putStringSet(keyName, stringSet)
        editor.apply()
    }

    fun saveToPreferences(keyName: String, status: Boolean, context:Context) {
        val editor: SharedPreferences.Editor = getSharedPref(context).edit()
        editor.putBoolean(keyName, status)
        editor.apply()
    }

    fun getStringSetFromPreferences(keyName: String, context:Context): Set<String>? {
        return getSharedPref(context).getStringSet(keyName, null)
    }

    fun getBooleanFromPreferences(keyName: String, context:Context): Boolean {
        return getSharedPref(context).getBoolean(keyName, false)
    }



