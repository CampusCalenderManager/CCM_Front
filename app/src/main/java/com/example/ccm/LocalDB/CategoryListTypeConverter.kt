package com.example.ccm.LocalDB

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.ccm.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@ProvidedTypeConverter
class CategoryListTypeConverter(
    val moshi: Moshi
) {
    @TypeConverter
    fun fromString(value: String): List<Category>? {
        val listType = Types.newParameterizedType(List::class.java, Category::class.java)
        val adapter: JsonAdapter<List<Category>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromImage(type: List<Category>): String {
        val listType = Types.newParameterizedType(List::class.java, Category::class.java)
        val adapter: JsonAdapter<List<Category>> = moshi.adapter(listType)
        return adapter.toJson(type)
    }
}