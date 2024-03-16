package com.example.elevatorsimulator.utils

import android.content.Context

class TinyDbSingleton {
    companion object {
        private var tinyDB: TinyDB? = null

        fun getInstance(): TinyDB? {
            if (tinyDB != null) {
                return tinyDB
            } else {
                throw UninitializedPropertyAccessException("TinyDB is not initialized")
            }
        }

        fun init(context: Context) {
            tinyDB = TinyDB(context.applicationContext)
        }
    }
}
