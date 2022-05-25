package com.example.ccm.LocalDB

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.example.ccm.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class CalendarScheduleObjectListTypeConverter(
    val moshi: Moshi
) {
    @TypeConverter
    fun fromString(value: String): List<CalendarScheduleObject>? {
        val listType = Types.newParameterizedType(List::class.java, CalendarScheduleObject::class.java)
        val adapter: JsonAdapter<List<CalendarScheduleObject>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromImage(type: List<CalendarScheduleObject>): String {
        val listType = Types.newParameterizedType(List::class.java, CalendarScheduleObject::class.java)
        val adapter: JsonAdapter<List<CalendarScheduleObject>> = moshi.adapter(listType)
        return adapter.toJson(type)
    }
}