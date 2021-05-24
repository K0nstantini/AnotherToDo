package com.homemade.anothertodo.single_tasks.list

import androidx.lifecycle.*
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.Event
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleTaskListViewModel @Inject constructor (private val repo: Repository) : ViewModel() {

    val tasks: LiveData<List<SingleTask>> = repo.singleTasks.asLiveData()

    private var _actionMode = MutableLiveData<PrimaryActionModeCallback?>()
    val actionMode = Transformations.map(_actionMode) { it != null }

    private val _navigateToEdit = MutableLiveData<Event<SingleTask?>>()
    val navigateToEdit: LiveData<Event<SingleTask?>> get() = _navigateToEdit

    private val _selectedItem = MutableLiveData<List<Int>>()
    val selectedItem: LiveData<List<Int>> get() = _selectedItem

    private var _currentItem: SingleTask? = null
    val currentItem: SingleTask? get() = _currentItem

    fun onAddClicked() {
        // TODO
    }

    fun onEditClicked() {
        // TODO
    }

    fun onDeleteClicked() {
        // TODO
    }

    fun destroyActionMode() {
        _actionMode.value?.finishActionMode()
        _actionMode.value = null
        _selectedItem.value = listOf()
        _currentItem = null
    }

}