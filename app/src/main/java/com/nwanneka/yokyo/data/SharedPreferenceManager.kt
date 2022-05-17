package com.nwanneka.yokyo.data

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject


class SharedPreferenceManager @Inject constructor(context: Context) {
    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "com.nwanneka.yokyo.UserPreferences"
        private const val USER_PASS = "userPassword"

        private lateinit var preferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
    }

    init {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)!!
        editor = preferences.edit()
    }

    fun setUserPassword(pass: String) {
        preferences.edit().putString(USER_PASS, pass).apply()
    }

    fun getOldPassword(): String? {
        return preferences.getString(USER_PASS, null)
    }

}