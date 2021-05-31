package com.homemade.anothertodo.add_classes

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.homemade.anothertodo.dialogs.MyInputDialog
import com.homemade.anothertodo.utils.Event
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

abstract class BaseViewModel : ViewModel() {

    private val _showInputDialog = MutableLiveData<Event<MyInputDialog>>()
    val showInputDialog: LiveData<Event<MyInputDialog>> get() = _showInputDialog

    private val _message = MutableLiveData<Event<@StringRes Int>>()
    val message: LiveData<Event<Int>> get() = _message

    fun setInputDialog(dialog: MyInputDialog) {
        _showInputDialog.value = Event(dialog)
    }

    fun setMessage(@StringRes res: Int) = _message.apply { value = Event(res) }

}