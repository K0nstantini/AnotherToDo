package com.homemade.anothertodo.task_list

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TASK_MODE_KEY = "taskModeKey"
const val TASK_TYPE_KEY = "taskTypeKey"

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel() {

    val mode: TaskListMode = handle.get<TaskListMode>(TASK_MODE_KEY) ?: TaskListMode.DEFAULT
    val taskType: TypeTask = handle.get<TypeTask>(TASK_TYPE_KEY) ?: TypeTask.REGULAR_TASK

    val title: Int = when (taskType) {
        TypeTask.REGULAR_TASK -> mode.titleRegularTask
        TypeTask.SINGLE_TASK -> mode.titleSingleTask
    }

    private val allTasks: LiveData<List<Task>> = repo.getTasksFlow(taskType).asLiveData()

    val shownTasks: LiveData<List<Task>> = Transformations.map(allTasks) { tasks ->
        getTasksToShow(if (mode == TaskListMode.SELECT_CATALOG) getOpenGroups() else tasks)
    }

    val levels: LiveData<Map<Long, Int>> = Transformations.map(allTasks) { tasks ->
        tasks.associateBy({ it.id }, { level(it) })
    }

    private var isActionMode: Boolean = false

    private val _showActionMode = MutableLiveData<Event<Boolean>>()
    val showActionMode: LiveData<Event<Boolean>> get() = _showActionMode

    private val _hideActionMode = MutableLiveData<Event<Boolean>>()
    val hideActionMode: LiveData<Event<Boolean>> get() = _hideActionMode

    // TODO: CHeck if work
    val showAddButton: LiveData<Boolean>
        get() = liveData { emit(!isActionMode && mode.showAddBtn) }

    // Доступность кнопки подтверждения при выборе задачи (режим "Выбор каталога/задачи")
    private val _enabledConfirmMenu = MutableLiveData<Boolean>(null)
    val enabledConfirmMenu: LiveData<Boolean> get() = _enabledConfirmMenu

    private var currentTask = MutableLiveData<Task?>(null)
    val currentTaskName: String get() = currentTask.value?.name ?: String()
    val currentTaskID: Long get() = currentTask.value?.id ?: -1

    // Список выделенных задач (напр. для удаления)
    private val _selectedItems = MutableLiveData<List<Int>>()
    val selectedItems: LiveData<List<Int>> get() = _selectedItems
    private val selectedTasks: List<Task>
        get() = _selectedItems.value?.map { getTask(it) ?: Task() } ?: emptyList()

    private val _navigateToEdit = MutableLiveData<Event<Task?>>()
    val navigateToEdit: LiveData<Event<Task?>> get() = _navigateToEdit

    private val _navigateToAdd = MutableLiveData<Event<Boolean?>>()
    val navigateToAdd: LiveData<Event<Boolean?>> get() = _navigateToAdd

    fun onAddClicked() = _navigateToAdd.apply { value = Event(true) }
    fun onEditClicked() = _navigateToEdit.apply { value = Event(currentTask.value) }

    fun onDeleteClicked() {
        selectedTasks.delete()
        destroyActionMode()
    }

    fun onItemClicked(task: Task) {
        currentTask.value = task
        when {
            isActionMode -> selectItemActionMode(task)
            isMarkTaskForSelection(mode, task) -> selectTaskForSelectionMode(task)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onItemLongClicked(task: Task) =
        if (mode.supportLongClick) {
            currentTask.value = task
            if (!isActionMode) {
                setActionMode()
            }
            selectItemActionMode(task)
            true
        } else {
            false
        }

    private fun selectItemActionMode(task: Task) {
        val selectedList = _selectedItems.value ?: emptyList()
        val position = task.position()
        _selectedItems.value = if (selectedList.contains(position)) {
            selectedList - position
        } else {
            selectedList + position
        }
        if (_selectedItems.value.isNullOrEmpty()) {
            destroyActionMode()
        }
    }

    private fun setActionMode() {
        _showActionMode.value = Event(true)
        isActionMode = true
    }

    fun destroyActionMode() {
        if (isActionMode) {
            isActionMode = false
            currentTask.value = null
            _selectedItems.value = emptyList()
            _hideActionMode.value = Event(true)
        }
    }

    private fun isMarkTaskForSelection(mode: TaskListMode, task: Task): Boolean =
        (mode == TaskListMode.SELECT_CATALOG) || (mode == TaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: Task) {
        val position = task.position()
        _selectedItems.value = listOf(position)
        _enabledConfirmMenu.value = position >= 0
    }

    private fun setGroupOpenClose(task: Task) = task.apply { groupOpen = !groupOpen }.update()

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

    private fun Task.position() = shownTasks.value?.indexOf(this) ?: -1

    private fun task(id: Long) = allTasks.value?.find { it.id == id }
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

    private fun Task.update() =
        viewModelScope.launch { repo.updateTask(this@update) }

    private fun List<Task>.delete() =
        viewModelScope.launch { repo.deleteTasks(this@delete) }

    private fun getOpenGroups(): List<Task> {
        val groups = mutableListOf<Task>()
        allTasks.value?.filter { it.group }?.forEach {
            groups.add(it.copy(groupOpen = true))
        }
        return groups
    }

}