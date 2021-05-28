package com.homemade.anothertodo.single_tasks.list

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.Event
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleTaskListViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    val tasks: LiveData<List<SingleTask>> = repo.singleTasks.asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var _actionMode = MutableLiveData<PrimaryActionModeCallback?>()
    val actionMode = Transformations.map(_actionMode) { it != null }

    private val _navigateToEdit = MutableLiveData<Event<SingleTask?>>()
    val navigateToEdit: LiveData<Event<SingleTask?>> get() = _navigateToEdit

    private val _navigateToAdd = MutableLiveData<Event<Boolean?>>()
    val navigateToAdd: LiveData<Event<Boolean?>> get() = _navigateToAdd

    private val _selectedItem = MutableLiveData<List<Int>>()
    val selectedItem: LiveData<List<Int>> get() = _selectedItem

    private var _currentItem: SingleTask? = null
    val currentItem: SingleTask? get() = _currentItem

    fun onAddClicked() {
        _navigateToAdd.apply {
            value = Event(true)
        }
    }

    fun onEditClicked() {
        _navigateToEdit.value = Event(_currentItem)
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            _selectedItem.value?.let { selected ->
                val items = selected.map { getTask(it) ?: SingleTask() }
                repo.deleteSingleTasks(items)
            }
        }
        _actionMode.value?.finishActionMode()
        destroyActionMode()
    }

    fun onItemClicked(task: SingleTask) {
        _actionMode.value?.let {
            _currentItem = task
            selectItemActionMode(task)
        }
    }

    fun onItemLongClicked(task: SingleTask): Boolean {
        _currentItem = task
        if (_actionMode.value == null) {
            val mode = PrimaryActionModeCallback()
            _actionMode.value = mode
            _showActionMode.value = Event(mode)
            _selectedItem.value = listOf(getPosition(task))
        } else {
            selectItemActionMode(task)
        }
        return true
    }

    private fun selectItemActionMode(task: SingleTask) {
        val list = _selectedItem.value?.toMutableList()
        val position = getPosition(task)
        if (list?.contains(position) == true) {
            list.remove(position)
            if (list.isNullOrEmpty()) {
                destroyActionMode()
            }
        } else {
            list?.add(position)
        }
        _selectedItem.value = list ?: mutableListOf()
    }

    fun destroyActionMode() {
        _actionMode.value?.finishActionMode()
        _actionMode.value = null
        _selectedItem.value = listOf()
        _currentItem = null
    }

    private fun getPosition(task: SingleTask) = tasks.value?.indexOf(task) ?: -1
    private fun getTask(index: Int) = tasks.value?.getOrNull(index)
}
