package com.example.elevatorsimulator.utils

import android.content.Context

class TinyDbSingleton {
    private var tinyDB: TinyDB? = null

    fun getInstance(context: Context): TinyDB? {
        if (tinyDB == null) {
            tinyDB = TinyDB((context.applicationContext))
        }
        return tinyDB
    }
}