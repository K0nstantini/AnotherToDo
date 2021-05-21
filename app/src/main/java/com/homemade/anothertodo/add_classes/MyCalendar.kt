package com.homemade.anothertodo.add_classes

import java.util.*

class MyCalendar(private val _milli: Long = 0L) {

    private val calendar = Calendar.getInstance().also { it.timeInMillis = _milli }

    val milli: Long
        get() = calendar.timeInMillis

    private val year: Int
        get() = calendar.get(Calendar.YEAR)

    private val month: Int
        get() = calendar.get(Calendar.MONTH)

    private val day: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)

    val hours: Int
        get() = calendar.get(Calendar.HOUR_OF_DAY)

    val minutes: Int
        get() = calendar.get(Calendar.MINUTE)

    fun set(y: Int = 0, m: Int = 0, d: Int = 0, h: Int = 0, min: Int = 0, s: Int = 0) =
        this.also { calendar.set(y, m, d, h, min, s) }

    private fun now() = this.also { calendar.timeInMillis = System.currentTimeMillis() }

    fun today() = this.now().set(year, month, day, 0, 0, 0)

}