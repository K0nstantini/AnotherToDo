package com.homemade.anothertodo.dialogs

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.homemade.anothertodo.R
import com.homemade.anothertodo.utils.toArray
import kotlin.reflect.KFunction1

class MySingleChoiceDialog(val funSave: KFunction1<Int, Unit>, private var selectedItem: Int = 0) {
    private var items = arrayOf<String>()
    private var resItems = 0

    private var title = ""
    private var resTitle = 0

    fun setItems(@ArrayRes _res: Int) = this.apply { resItems = _res }
    fun setItems(_items: Array<String>) = this.apply { items = _items }
    fun setItems(_items: List<String>) = this.apply { items = _items.toTypedArray() }

    fun setTitle(@StringRes _resTitle: Int) = this.apply { resTitle = _resTitle }
    fun setTitle(_title: String) = this.apply { title = _title }

    fun show(activity: FragmentActivity) {
        if (resItems != 0) {
            items = resItems.toArray(activity.application)
        }
        MaterialAlertDialogBuilder(activity)
            .setTitle(if (resTitle != 0) activity.getString(resTitle) else title)
            .setSingleChoiceItems(items, selectedItem) { _, which -> selectedItem = which }
            .setPositiveButton(R.string.ok) { _, _ -> funSave(selectedItem) }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

}