package com.nwanneka.yokyo.core

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class YokyoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "Application Created")
    }
}