package com.homemade.anothertodo.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TypeTask : Parcelable {
    SINGLE_TASK,
    REGULAR_TASK,
}