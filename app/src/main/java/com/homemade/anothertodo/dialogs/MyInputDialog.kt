package com.homemade.anothertodo.dialogs

import android.text.InputFilter
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.homemade.anothertodo.R
import kotlin.reflect.KFunction1

class MyInputDialog(private val funSave: KFunction1<String, Unit>, private val value: String = "") {
    private var title = ""
    private var resTitle = 0

    private var message = ""
    private var resMessage = 0

    private var length = 0

    fun setTitle(@StringRes _resTitle: Int) = this.also { resTitle = _resTitle }
    fun setTitle(_title: String) = this.also { title = _title }

    fun setMessage(@StringRes _resMessage: Int) = this.also { resMessage = _resMessage }
    fun setMessage(_message: String) = this.also { message = _message }

    fun setLength(_length: Int) = this.also { length = _length }

    fun show(activity: FragmentActivity) {

        val inputView = activity.layoutInflater.inflate(R.layout.alert_input_text, null)

        val editText = inputView.findViewById<EditText>(R.id.editText)
        editText.apply {
            if (length > 0) {
                filters = arrayOf(InputFilter.LengthFilter(length))
            }
            setText(value)
        }

        val dialog = MaterialAlertDialogBuilder(activity)
            .setTitle(if (resTitle == 0) title else activity.getString(resTitle))
            .setMessage(if (resMessage == 0) message else activity.getString(resMessage))
            .setView(inputView)
            .setPositiveButton(R.string.ok) { _, _ -> funSave(editText.text.toString()) }
            .setNegativeButton(R.string.cancel) { _, _ -> }

        dialog.show()
    }

}