package com.homemade.anothertodo.db

import androidx.room.TypeConverter
import com.homemade.anothertodo.add_classes.MyCalendar

class Converters {

    @TypeConverter
    fun fromMyCalendar(date: MyCalendar) = date.milli

    @TypeConverter
    fun toMyCalendar(milli: Long) = MyCalendar(milli)
}