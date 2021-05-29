package com.homemade.anothertodo.single_tasks.list

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.Event
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SINGLE_TASK_MODE_KEY = "singleTaskModeKey"

@HiltViewModel
class SingleTaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : ViewModel() {

    val mode = handle.get<SingleTaskListMode>(SINGLE_TASK_MODE_KEY) ?: SingleTaskListMode.DEFAULT
    val title = mode.title

    val allSingleTasks: LiveData<List<SingleTask>> = repo.singleTasks.asLiveData()
    val allGroups: LiveData<List<SingleTask>> = repo.singleTasksGroups.asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var _actionMode = MutableLiveData<PrimaryActionModeCallback?>()
    val actionMode = Transformations.map(_actionMode) { it != null }

    // Доступность кнопки подтверждения при выборе задачи (режим "Выбор каталога")
    private val _enabledConfirmMenu = MutableLiveData<Boolean>(null)
    val enabledConfirmMenu: LiveData<Boolean> get() = _enabledConfirmMenu

    // Визуальное отображение списка задач
    val shownTasks: LiveData<List<SingleTask>> = Transformations.map(allSingleTasks) { tasks ->
        getTasksToShow(if (mode == SingleTaskListMode.SELECT_CATALOG) getOpenGroups() else tasks)
    }

    // Текущая выделенная задача
    private var currentTask: SingleTask? = null
    val currentTaskID: Long? get() = currentTask?.id

    // Уровень вложенности задачи
    val levels: LiveData<Map<Long, Int>> = Transformations.map(allSingleTasks) { tasks ->
        tasks.associateBy({ it.id }, { level(it) })
    }

    private val _navigateToEdit = MutableLiveData<Event<SingleTask?>>()
    val navigateToEdit: LiveData<Event<SingleTask?>> get() = _navigateToEdit

    private val _navigateToAdd = MutableLiveData<Event<Boolean?>>()
    val navigateToAdd: LiveData<Event<Boolean?>> get() = _navigateToAdd

    private val _selectedItem = MutableLiveData<List<Int>>()
    val selectedItem: LiveData<List<Int>> get() = _selectedItem

    fun onAddClicked() {
        _navigateToAdd.apply {
            value = Event(true)
        }
    }

    fun onEditClicked() {
        _navigateToEdit.value = Event(currentTask)
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
        currentTask = task
        if (_actionMode.value == null) {
            selectItem(task)
        } else {
            indexTask(task)?.let { selectItemActionMode(task) }
        }
    }

    fun onItemLongClicked(task: SingleTask): Boolean {
        currentTask = task
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

    private fun selectItem(task: SingleTask) {
        when {
            mode == SingleTaskListMode.SELECT_CATALOG -> confirmInSelectCatalog(task)
            mode == SingleTaskListMode.SELECT_TASK && !task.group -> confirmInSelectCatalog(task)
            else -> if (task.group) {
                setGroupOpenClose(task)
                _selectedItem.value = emptyList()
                _enabledConfirmMenu.value = false
            }
        }
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
        currentTask = null
    }

    private fun confirmInSelectCatalog(task: SingleTask) {
        val index = indexTask(task)
        index?.let {
            _selectedItem.value = listOf(it)
            _enabledConfirmMenu.value = index >= 0 &&
                    !(mode == SingleTaskListMode.SELECT_TASK && task.group)
        }
    }

    private fun setGroupOpenClose(task: SingleTask) = viewModelScope.launch {
        task.groupOpen = !task.groupOpen
        repo.updateSingleTask(task)
    }

    private fun getTasksToShow(
        tasks: List<SingleTask>,
        id: Long = 0,
        list: MutableList<SingleTask> = mutableListOf()
    ): List<SingleTask> {

        val comparator = compareByDescending<SingleTask> { it.group }.thenBy { it.name }
        tasks.filter { it.parent == id }
            .sortedWith(comparator)
            .forEach {
                list.add(it)
                if (it.groupOpen) {
                    getTasksToShow(tasks, it.id, list)
                }
            }
        return list
    }

    private fun getOpenGroups(): List<SingleTask> {
        val groups = mutableListOf<SingleTask>()
        allSingleTasks.value?.filter { it.group }?.forEach {
            groups.add(it.copy(groupOpen = true))
        }
        return groups
    }

    private fun task(id: Long) = allSingleTasks.value?.find { it.id == id }
    private fun task(index: Int) = shownTasks.value?.getOrNull(index)
    private fun getPosition(task: SingleTask) = allSingleTasks.value?.indexOf(task) ?: -1
    private fun getTask(index: Int) = allSingleTasks.value?.getOrNull(index)
    private fun indexTask(task: SingleTask) =
        shownTasks.value?.let { tasks -> tasks.indexOf(tasks.find { it.id == task.id }) }

    private fun level(task: SingleTask): Int {
        var level = 0
        var parentId = task.parent
        while (parentId != 0L) {
            level++
            parentId = task(parentId)?.parent ?: 0L
        }
        return level
    }
}
