package com.homemade.anothertodo.utils

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.homemade.anothertodo.R
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.db.entity.SingleTask

@BindingAdapter("singleTaskImage")
fun ImageView.setTaskIcon(item: SingleTask?) {
    item?.let {
        setImageResource(
            when {
                item.groupOpen -> R.drawable.ic_folder_open
                item.group -> R.drawable.ic_folder
                else -> R.drawable.ic_file
            }
        )
    }
}

@BindingAdapter("singleTaskListTaskName")
fun TextView.setTaskListTaskName(item: SingleTask?) {
    item?.let {
        text = item.name
        typeface = if (item.group) {
            setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            Typeface.DEFAULT_BOLD
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.colorTextDefault))
            Typeface.DEFAULT
        }
    }
}

@BindingAdapter("singleTaskDeadline")
fun TextView.setSingleTaskDeadline(item: SingleTask?) {
    item?.let {
        val date = item.dateActivation + MyCalendar(item.deadline.hoursToMilli())
        val sDate = date.toString(true)
        text = resources.getString(R.string.main_screen_single_task_second_text, sDate)
    }
}