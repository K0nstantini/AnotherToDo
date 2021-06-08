package com.homemade.anothertodo.add_classes

import android.os.Parcel
import android.os.Parcelable
import com.homemade.anothertodo.utils.MINUTES_IN_HOUR
import com.homemade.anothertodo.utils.toStrTime
import java.util.*

class MyCalendar(private val _milli: Long = 0L) : Parcelable {
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

    private val time: String
        get() = (hours * MINUTES_IN_HOUR + minutes).toStrTime()

    override fun toString(): String {
        val y = year.toString().padStart(4, '0')
        val m = (month + 1).toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')

        return "$d.$m.$y $time"
    }

    fun toString(showTime: Boolean): String {
        return if (showTime) toString() else toString().dropLast(6)
    }

    fun set(y: Int = 0, m: Int = 0, d: Int = 0, h: Int = 0, min: Int = 0, s: Int = 0) =
        this.also { calendar.set(y, m, d, h, min, s) }

    fun now() = this.also { calendar.timeInMillis = System.currentTimeMillis() }

    fun today() = this.now().set(year, month, day, 0, 0, 0)

    fun isEmpty() = milli == 0L
    fun isNoEmpty() = milli != 0L

    constructor(parcel: Parcel) : this(parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(_milli)
    }

    operator fun compareTo(other: MyCalendar) = when {
        milli > other.milli -> 1
        milli < other.milli -> -1
        else -> 0
    }

    operator fun minus(other: MyCalendar) = MyCalendar(milli - other.milli)
    operator fun plus(other: MyCalendar) = MyCalendar(milli + other.milli)

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MyCalendar> {
        override fun createFromParcel(parcel: Parcel): MyCalendar {
            return MyCalendar(parcel)
        }

        override fun newArray(size: Int): Array<MyCalendar?> {
            return arrayOfNulls(size)
        }
    }

}