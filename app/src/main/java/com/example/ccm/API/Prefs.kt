package com.example.ccm.API

import android.content.Context

// 토큰을 앱에 저장하기 위한 클래스
class Prefs(context: Context) {
    private val prefName = "mPref"
    private val prefs = context.getSharedPreferences(prefName, 0)

    var token: String?
    get() = prefs.getString("token", null)
    set(value) = prefs.edit().putString("token", value).apply()
}