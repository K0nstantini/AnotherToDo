package com.homemade.anothertodo.add_classes

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homemade.anothertodo.dialogs.MyConfirmAlertDialog
import com.homemade.anothertodo.dialogs.MyInputDialog
import com.homemade.anothertodo.utils.Event

abstract class BaseViewModel : ViewModel() {

    private val _showInputDialog = MutableLiveData<Event<MyInputDialog>>()
    val showInputDialog: LiveData<Event<MyInputDialog>> get() = _showInputDialog

    private val _showConfirmDialog = MutableLiveData<Event<MyConfirmAlertDialog>>()
    val showConfirmDialog: LiveData<Event<MyConfirmAlertDialog>> get() = _showConfirmDialog

    private val _message = MutableLiveData<Event<@StringRes Int>>()
    val message: LiveData<Event<Int>> get() = _message

    fun setInputDialog(dialog: MyInputDialog) {
        _showInputDialog.value = Event(dialog)
    }

    fun setConfirmDialog(dialogConfirm: MyConfirmAlertDialog) {
        _showConfirmDialog.value = Event(dialogConfirm)
    }

    fun setMessage(@StringRes res: Int) = _message.apply { value = Event(res) }

}