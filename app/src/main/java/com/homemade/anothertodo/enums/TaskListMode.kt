package com.homemade.anothertodo.enums

import android.os.Parcelable
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import com.homemade.anothertodo.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TaskListMode(
    @MenuRes val menu: Int?,
    @StringRes val titleSingleTask: Int,
    @StringRes val titleRegularTask: Int,
    val showAddBtn: Boolean,
    val supportLongClick: Boolean
) : Parcelable {

    DEFAULT(
        null,
        R.string.title_single_task_list,
        R.string.title_regular_task_list,
        true,
        true
    ),
    SELECT_CATALOG(
        R.menu.confirm_menu,
        R.string.title_select_catalog,
        R.string.title_select_catalog,
        false,
        false
    ),
    SELECT_TASK(
        R.menu.confirm_menu,
        R.string.title_select_task,
        R.string.title_select_task,
        false,
        false
    )
}