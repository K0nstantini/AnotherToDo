package com.homemade.anothertodo.db

import androidx.room.TypeConverter
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.enums.TypeTask

class Converters {

    @TypeConverter
    fun fromMyCalendar(date: MyCalendar) = date.milli

    @TypeConverter
    fun toMyCalendar(milli: Long) = MyCalendar(milli)

    @TypeConverter
    fun fromTypeTask(typeTask: TypeTask) = typeTask.name

    @TypeConverter
    fun toCalendar(name: String) = TypeTask.valueOf(name)
}