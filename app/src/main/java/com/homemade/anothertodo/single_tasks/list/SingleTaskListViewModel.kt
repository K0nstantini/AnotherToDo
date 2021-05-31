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

    val allSingleTasks: LiveData<List<SingleTask>> = repo.singleTasksFlow.asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var actionMode = MutableLiveData<PrimaryActionModeCallback?>(null)
    private val isActionMode: Boolean get() = actionMode.value != null

    val showAddButton =
        Transformations.map(actionMode) { it == null && mode == SingleTaskListMode.DEFAULT }

    // Доступность кнопки подтверждения при выборе задачи (режим "Выбор каталога/задачи")
    private val _enabledConfirmMenu = MutableLiveData<Boolean>(null)
    val enabledConfirmMenu: LiveData<Boolean> get() = _enabledConfirmMenu


    // Визуальное отображение списка задач (при сворачивании/разворачивании групп)
    val shownTasks: LiveData<List<SingleTask>> = Transformations.map(allSingleTasks) { tasks ->
        getTasksToShow(if (mode == SingleTaskListMode.SELECT_CATALOG) getOpenGroups() else tasks)
    }

    // Текущая выделенная задача
    private var currentTask: SingleTask? = null
    val currentTaskID: Long get() = currentTask?.id ?: -1

    // Список выделенных задач (напр. для удаления)
    private val _selectedItems = MutableLiveData<List<Int>>()
    val selectedItems: LiveData<List<Int>> get() = _selectedItems
    private val selectedTasks: List<SingleTask>
        get() = _selectedItems.value?.map { getTask(it) ?: SingleTask() } ?: emptyList()

    // Уровень вложенности задач
    val levels: LiveData<Map<Long, Int>> = Transformations.map(allSingleTasks) { tasks ->
        tasks.associateBy({ it.id }, { level(it) })
    }

    private val _navigateToEdit = MutableLiveData<Event<SingleTask?>>()
    val navigateToEdit: LiveData<Event<SingleTask?>> get() = _navigateToEdit

    private val _navigateToAdd = MutableLiveData<Event<Boolean?>>()
    val navigateToAdd: LiveData<Event<Boolean?>> get() = _navigateToAdd

    fun onAddClicked() = _navigateToAdd.apply { value = Event(true) }
    fun onEditClicked() = _navigateToEdit.apply { value = Event(currentTask) }

    fun onDeleteClicked() {
        deleteTasksFromBase(selectedTasks)
        destroyActionMode()
    }

    fun onItemClicked(task: SingleTask) {
        currentTask = task
        when {
            isActionMode -> selectItemActionMode(task)
            isMarkTaskForSelection(mode, task) -> selectTaskForSelectionMode(task)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onItemLongClicked(task: SingleTask) =
        if (mode.supportLongClick) {
            currentTask = task
            if (actionMode.value == null) {
                setActionMode()
            }
            selectItemActionMode(task)
            true
        } else {
            false
        }

    private fun selectItemActionMode(task: SingleTask) {
        val selectedList = _selectedItems.value ?: emptyList()
        val position = getPosition(task)
        _selectedItems.value = if (selectedList.contains(position))
            selectedList - position
        else
            selectedList + position
        if (_selectedItems.value.isNullOrEmpty()) {
            destroyActionMode()
        }
    }

    private fun setActionMode() {
        val actMode = PrimaryActionModeCallback()
        actionMode.value = actMode
        _showActionMode.value = Event(actMode)
    }

    fun destroyActionMode() {
        actionMode.value?.finishActionMode()
        actionMode.value = null
        _selectedItems.value = emptyList()
        currentTask = null
    }

    private fun isMarkTaskForSelection(mode: SingleTaskListMode, task: SingleTask): Boolean =
        (mode == SingleTaskListMode.SELECT_CATALOG) ||
                (mode == SingleTaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: SingleTask) {
        val position = getPosition(task)
        _selectedItems.value = listOf(position)
        _enabledConfirmMenu.value = position >= 0
    }

    private fun setGroupOpenClose(task: SingleTask) {
        task.groupOpen = !task.groupOpen
        updateTaskInBase(task)
    }

    // FIXME: Не чистая ф-я?
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
    private fun getPosition(task: SingleTask) = shownTasks.value?.indexOf(task) ?: -1
    private fun getTask(index: Int): SingleTask? = allSingleTasks.value?.getOrNull(index)

    private fun level(task: SingleTask): Int {
        var level = 0
        var parentId = task.parent
        while (parentId != 0L) {
            level++
            parentId = task(parentId)?.parent ?: 0L
        }
        return level
    }

    private fun updateTaskInBase(task: SingleTask) = viewModelScope.launch {
        repo.updateSingleTask(task)
    }

    private fun deleteTasksFromBase(tasks: List<SingleTask>) = viewModelScope.launch {
        repo.deleteSingleTasks(tasks)
    }

}
