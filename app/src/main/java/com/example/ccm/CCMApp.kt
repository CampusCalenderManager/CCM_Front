package com.example.ccm

import android.app.Application
import com.example.ccm.LocalDB.UserDatabase

/**
 * 휴대전화 자체에 데이터를 저장하기 위한 싱글톤 클래스
 */
class CCMApp : Application() {
    companion object{
        lateinit var userLocalDB : UserDatabase
    }

    override fun onCreate() {
        userLocalDB = UserDatabase.getInstance(applicationContext)!!
        super.onCreate()
    }
}