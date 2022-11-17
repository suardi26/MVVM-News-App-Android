package com.practice.mvvm_news_app_android.db

import androidx.room.TypeConverter
import com.practice.mvvm_news_app_android.model.Source

class Converters {

    // Mengambil name dalam class Source
    @TypeConverter
    fun fromSource(source: Source): String?{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}