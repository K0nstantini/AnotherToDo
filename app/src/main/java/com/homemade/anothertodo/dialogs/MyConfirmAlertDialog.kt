package com.homemade.anothertodo.dialogs

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.homemade.anothertodo.R
import kotlin.reflect.KFunction0

class MyConfirmAlertDialog(private val funSave: KFunction0<Unit>) {
    private var title = ""
    private var resTitle = 0

    private var message = ""
    private var resMessage = 0

    private var funCancel: KFunction0<Unit>? = null

    fun setTitle(@StringRes _resTitle: Int) = this.also { resTitle = _resTitle }
    fun setTitle(_title: String) = this.also { title = _title }

    fun setMessage(@StringRes _resMessage: Int) = this.also { resMessage = _resMessage }
    fun setMessage(_message: String) = this.also { message = _message }

    fun setCancelListener(_funCancel: KFunction0<Unit>) = this.apply { funCancel = _funCancel }

    fun show(activity: FragmentActivity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(if (resTitle == 0) title else activity.getString(resTitle))
            .setMessage(if (resMessage == 0) message else activity.getString(resMessage))
            .setPositiveButton(R.string.ok) { _, _ -> funSave() }
            .setNegativeButton(R.string.cancel) { _, _ -> funCancel?.invoke() }
            .setOnCancelListener { funCancel?.invoke() }
            .show()
    }

}