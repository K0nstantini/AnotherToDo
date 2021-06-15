package com.homemade.anothertodo.add_classes

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.anothertodo.dialogs.MyConfirmAlertDialog
import com.homemade.anothertodo.dialogs.MyInputDialog
import com.homemade.anothertodo.dialogs.MySingleChoiceDialog
import com.homemade.anothertodo.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<E> : ViewModel() {

    private val _viewEvents = MutableSharedFlow<E>()

    val viewEvents: SharedFlow<E> = _viewEvents.asSharedFlow()

    protected fun setEvent(event: E) =
        viewModelScope.launch { _viewEvents.emit(event) }

    protected fun getEvents() = _viewEvents


    private val _showSingleChoiceDialog = MutableLiveData<Event<MySingleChoiceDialog>>()
    val showSingleChoiceDialog: LiveData<Event<MySingleChoiceDialog>>
        get() = _showSingleChoiceDialog

    private val _showInputDialog = MutableLiveData<Event<MyInputDialog>>()
    val showInputDialog: LiveData<Event<MyInputDialog>> get() = _showInputDialog

    private val _showConfirmDialog = MutableLiveData<Event<MyConfirmAlertDialog>>()
    val showConfirmDialog: LiveData<Event<MyConfirmAlertDialog>> get() = _showConfirmDialog

    private val _message = MutableLiveData<Event<@StringRes Int>>()
    val message: LiveData<Event<Int>> get() = _message

    fun setSingleChoiceDialog(dialog: MySingleChoiceDialog) {
        _showSingleChoiceDialog.value = Event(dialog)
    }

    fun setInputDialog(dialog: MyInputDialog) {
        _showInputDialog.value = Event(dialog)
    }

    fun setConfirmDialog(dialogConfirm: MyConfirmAlertDialog) {
        _showConfirmDialog.value = Event(dialogConfirm)
    }

    fun setMessage(@StringRes res: Int) = _message.apply { value = Event(res) }

}