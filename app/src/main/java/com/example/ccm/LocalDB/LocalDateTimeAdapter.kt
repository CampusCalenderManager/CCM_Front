package com.example.ccm.LocalDB

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LocalDateTimeAdapter {
    @RequiresApi(Build.VERSION_CODES.O)
    @ToJson
    fun toJson(value: java.time.LocalDateTime): String {
        val splitString = value.toString().split("T")
        val dateString = splitString[0] + "'T'" + splitString[1]
        Log.e("string", dateString)
        return ZonedDateTime.now()
            .format(DateTimeFormatter.ofPattern(
                dateString, Locale.getDefault()
            ))
    }

    @FromJson
    @RequiresApi(Build.VERSION_CODES.O)
    fun fromJson(value: String): java.time.LocalDateTime {
        return java.time.LocalDateTime.parse(value + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}