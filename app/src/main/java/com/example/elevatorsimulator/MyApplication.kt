package com.example.elevatorsimulator

import android.app.Application
import com.example.elevatorsimulator.utils.TinyDbSingleton

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TinyDbSingleton.init(applicationContext)
    }
}