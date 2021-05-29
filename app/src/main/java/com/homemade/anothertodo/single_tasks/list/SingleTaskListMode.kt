package com.homemade.anothertodo.single_tasks.list

import android.os.Parcelable
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import com.homemade.anothertodo.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SingleTaskListMode(
    @MenuRes val menu: Int?,
    @StringRes val title: Int,
    val showAddBtn: Boolean,
    val supportLongClick: Boolean
) : Parcelable {

    DEFAULT(
        null,
        R.string.title_single_task_list,
        true,
        true
    ),
    SELECT_CATALOG(
        R.menu.confirm_menu,
        R.string.title_single_task_select_catalog,
        false,
        false
    ),
    SELECT_TASK(
        R.menu.confirm_menu,
        R.string.title_single_task_select_task,
        false,
        false
    )
}