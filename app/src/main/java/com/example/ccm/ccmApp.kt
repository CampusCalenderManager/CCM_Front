package com.example.ccm

import android.app.Application
import com.example.ccm.API.Prefs

class ccmApp : Application() {
    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}