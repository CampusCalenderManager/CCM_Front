package com.example.ccm

import android.app.Application
import com.example.ccm.API.Prefs

/**
 * 휴대전화 자체에 데이터를 저장하기 위한 싱글톤 클래스
 */
class CCMApp : Application() {
    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}