package com.homemade.anothertodo.single_task.list

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.utils.Event
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SINGLE_TASK_MODE_KEY = "taskModeKey"

@HiltViewModel
class SingleTaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : ViewModel() {

    val mode = handle.get<TaskListMode>(SINGLE_TASK_MODE_KEY) ?: TaskListMode.DEFAULT
    val title = mode.titleSingleTask

    val allTasks: LiveData<List<Task>> = repo.getTasksFlow(TypeTask.SINGLE_TASK).asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var actionMode = MutableLiveData<PrimaryActionModeCallback?>(null)
    private val isActionMode: Boolean get() = actionMode.value != null

    val showAddButton =
        Transformations.map(actionMode) { it == null && mode == TaskListMode.DEFAULT }

    // Доступность кнопки подтверждения при выборе задачи (режим "Выбор каталога/задачи")
    private val _enabledConfirmMenu = MutableLiveData<Boolean>(null)
    val enabledConfirmMenu: LiveData<Boolean> get() = _enabledConfirmMenu


    // Визуальное отображение списка задач (при сворачивании/разворачивании групп)
    val shownTasks: LiveData<List<Task>> = Transformations.map(allTasks) { tasks ->
        getTasksToShow(if (mode == TaskListMode.SELECT_CATALOG) getOpenGroups() else tasks)
    }

    // Текущая выделенная задача
    private var currentTask: Task? = null
    val currentTaskID: Long get() = currentTask?.id ?: -1

    // Список выделенных задач (напр. для удаления)
    private val _selectedItems = MutableLiveData<List<Int>>()
    val selectedItems: LiveData<List<Int>> get() = _selectedItems
    private val selectedTasks: List<Task>
        get() = _selectedItems.value?.map { getTask(it) ?: Task() } ?: emptyList()

    // Уровень вложенности задач
    val levels: LiveData<Map<Long, Int>> = Transformations.map(allTasks) { tasks ->
        tasks.associateBy({ it.id }, { level(it) })
    }

    private val _navigateToEdit = MutableLiveData<Event<Task?>>()
    val navigateToEdit: LiveData<Event<Task?>> get() = _navigateToEdit

    private val _navigateToAdd = MutableLiveData<Event<Boolean?>>()
    val navigateToAdd: LiveData<Event<Boolean?>> get() = _navigateToAdd

    fun onAddClicked() = _navigateToAdd.apply { value = Event(true) }
    fun onEditClicked() = _navigateToEdit.apply { value = Event(currentTask) }

    fun onDeleteClicked() {
        deleteTasksFromBase(selectedTasks)
        destroyActionMode()
    }

    fun onItemClicked(task: Task) {
        currentTask = task
        when {
            isActionMode -> selectItemActionMode(task)
            isMarkTaskForSelection(mode, task) -> selectTaskForSelectionMode(task)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onItemLongClicked(task: Task) =
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

    private fun selectItemActionMode(task: Task) {
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

    private fun isMarkTaskForSelection(mode: TaskListMode, task: Task): Boolean =
        (mode == TaskListMode.SELECT_CATALOG) ||
                (mode == TaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: Task) {
        val position = getPosition(task)
        _selectedItems.value = listOf(position)
        _enabledConfirmMenu.value = position >= 0
    }

    private fun setGroupOpenClose(task: Task) {
        task.groupOpen = !task.groupOpen
        updateTaskInBase(task)
    }

    // FIXME: Не чистая ф-я?
    private fun getTasksToShow(
        tasks: List<Task>,
        id: Long = 0,
        list: MutableList<Task> = mutableListOf()
    ): List<Task> {

        val comparator = compareByDescending<Task> { it.group }.thenBy { it.name }
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

    private fun getOpenGroups(): List<Task> {
        val groups = mutableListOf<Task>()
        allTasks.value?.filter { it.group }?.forEach {
            groups.add(it.copy(groupOpen = true))
        }
        return groups
    }

    private fun task(id: Long) = allTasks.value?.find { it.id == id }
    private fun getPosition(task: Task) = shownTasks.value?.indexOf(task) ?: -1
    private fun getTask(index: Int): Task? = allTasks.value?.getOrNull(index)

    private fun level(task: Task): Int {
        var level = 0
        var parentId = task.parent
        while (parentId != 0L) {
            level++
            parentId = task(parentId)?.parent ?: 0L
        }
        return level
    }

    private fun updateTaskInBase(task: Task) = viewModelScope.launch {
        repo.updateSingleTask(task)
    }

    private fun deleteTasksFromBase(tasks: List<Task>) = viewModelScope.launch {
        repo.deleteSingleTasks(tasks)
    }

}