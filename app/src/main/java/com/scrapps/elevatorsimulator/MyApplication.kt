package com.scrapps.elevatorsimulator

import android.app.Application
import com.scrapps.elevatorsimulator.utils.TinyDbSingleton

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TinyDbSingleton.init(applicationContext)
    }
}