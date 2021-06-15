package com.homemade.anothertodo.task_list

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TASK_MODE_KEY = "taskModeKey"
const val TASK_TYPE_KEY = "taskTypeKey"

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel<TaskListViewModel.Event1>() {

    // TODO: Rename
    sealed class Event1 {
        data class NavigateToAddEdit(val task: Task? = null) : Event1()
        data class ShowActionMode(val show: Boolean) : Event1()
        data class EnabledConfirmMenuItem(val show: Boolean) : Event1()
        data class ShowDoneActionMenuItem(val show: Boolean) : Event1()
        data class ShowEditActionMenuItem(val show: Boolean) : Event1()
        data class ShowToast(@StringRes val res: Int) : Event1()
    }

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

    private val _enabledConfirmMenu = MutableLiveData<Boolean>(null)
    val enabledConfirmMenu: LiveData<Boolean> get() = _enabledConfirmMenu

    private var currentTask: Task? = null
    val currentTaskID: Long get() = currentTask?.id ?: -1 // FIXME: DEl?

    private val _selectedItems = MutableStateFlow(listOf<Int>())
    val selectedItems = _selectedItems.asStateFlow()

    // TODO: Del?
    private val selectedTasks: List<Task>
        get() = _selectedItems.value.map { getTask(it) ?: Task() }

    val actionModeTitle: StateFlow<String?> = _selectedItems
        .map { currentTask?.name }
        .asState(null)


    val showAddButton: StateFlow<Boolean> = getEvents()
        .filterIsInstance<Event1.ShowActionMode>()
        .map { mapToShowAddButton(it) }
        .asState(mode.showAddBtn)

    val showDoneActionMenu: StateFlow<Boolean> = _selectedItems
        .map { mapToShowDoneActionMenu(it) }
        .asState(false)

    val showEditActionMenu: StateFlow<Boolean> = _selectedItems
        .map { mapToEditAddButton(it) }
        .asState(false)


    private fun mapToShowAddButton(event: Event1.ShowActionMode) =
        !event.show && mode.showAddBtn

    private fun mapToShowDoneActionMenu(selected: List<Int>): Boolean {
        val noMultiSelect = selected.count() == 1
        val noGroup = !(currentTask?.group ?: false)
        return !isActionMode || (taskType == TypeTask.SINGLE_TASK && noGroup && noMultiSelect)
    }

    private fun mapToEditAddButton(selected: List<Int>) =
        !isActionMode || selected.count() == 1


    fun onAddClicked() =
        setEvent(Event1.NavigateToAddEdit())

    fun onEditClicked() {
        setEvent(Event1.NavigateToAddEdit(currentTask))
        destroyActionMode()
    }

    fun onDeleteClicked() {
        selectedTasks.delete()
        destroyActionMode()
    }

    fun onItemClicked(task: Task) {
        when {
            isActionMode -> selectItemActionMode(task)
            isMarkTaskForSelection(mode, task) -> selectTaskForSelectionMode(task)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onItemLongClicked(task: Task) =
        if (mode.supportLongClick) {
            if (!isActionMode) {
                setActionMode()
            }
            selectItemActionMode(task)
            true
        } else {
            false
        }

    private fun selectItemActionMode(task: Task) {
        val selectedListBefore = _selectedItems.value ?: emptyList()
        val listAfter = { list: List<Int>, pos: Int -> if (list.contains(pos)) list - pos else list + pos }
        val selectedListAfter = listAfter(selectedListBefore, task.position())

        currentTask = when (selectedListAfter.count()) {
            1 -> getTask(selectedListAfter[0])
            else -> null
        }
        _selectedItems.value = selectedListAfter
        if (selectedListAfter.isEmpty()) {
            destroyActionMode()
        }
    }

    private fun setActionMode() {
        setEvent(Event1.ShowActionMode(true))
        isActionMode = true
    }

    fun destroyActionMode() {
        if (isActionMode) {
            isActionMode = false
            currentTask = null
            _selectedItems.value = emptyList()
            setEvent(Event1.ShowActionMode(false))
        }
    }

    private fun isMarkTaskForSelection(mode: TaskListMode, task: Task): Boolean =
        (mode == TaskListMode.SELECT_CATALOG) || (mode == TaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: Task) {
        currentTask = task
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
    private fun getTask(index: Int): Task? = shownTasks.value?.getOrNull(index)

    private fun level(task: Task): Int {
        var level = 0
        var parentId = task.parent
        while (parentId != 0L) {
            level++
            parentId = task(parentId)?.parent ?: 0L
        }
        return level
    }

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun List<Task>.delete() = viewModelScope.launch { repo.deleteTasks(this@delete) }

    private fun getOpenGroups(): List<Task> {
        val groups = mutableListOf<Task>()
        allTasks.value?.filter { it.group }?.forEach {
            groups.add(it.copy(groupOpen = true))
        }
        return groups
    }

    private fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)

}