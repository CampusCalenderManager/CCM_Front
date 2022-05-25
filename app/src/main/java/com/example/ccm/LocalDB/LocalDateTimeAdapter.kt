package com.example.ccm.LocalDB

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    @RequiresApi(Build.VERSION_CODES.O)
    @ToJson
    fun toJson(value: java.time.LocalDateTime): String {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value)
    }

    @FromJson
    @RequiresApi(Build.VERSION_CODES.O)
    fun fromJson(value: String): java.time.LocalDateTime {
        return java.time.LocalDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}