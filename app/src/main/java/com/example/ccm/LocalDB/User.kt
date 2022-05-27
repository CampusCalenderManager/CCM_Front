package com.example.ccm.LocalDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.example.ccm.Category
import com.example.ccm.MainActivity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class User(
    @field:Json(name = "username") var username: String? = null,
    @field:Json(name = "userToken") var userToken: String? = null,
    @field:Json(name = "userCategory") var userCategory: List<Category>? = null,
    @field:Json(name = "userSchedule") var userSchedule: List<CalendarScheduleObject>? = null
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}