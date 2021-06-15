package com.homemade.anothertodo.task_list

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TASK_MODE_KEY = "taskModeKey"
const val TASK_TYPE_KEY = "taskTypeKey"

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel<TaskListViewModel.Event>() {

    sealed class Event {
        data class NavigateToAddEdit(val task: Task? = null) : Event()
        data class ShowActionMode(val show: Boolean) : Event()
    }

    /** Variables static */

    val mode: TaskListMode = handle.get<TaskListMode>(TASK_MODE_KEY) ?: TaskListMode.DEFAULT
    val taskType: TypeTask = handle.get<TypeTask>(TASK_TYPE_KEY) ?: TypeTask.REGULAR_TASK

    val title: Int = when (taskType) {
        TypeTask.REGULAR_TASK -> mode.titleRegularTask
        TypeTask.SINGLE_TASK -> mode.titleSingleTask
    }

    /** Variables flow */

    private val allTasks: StateFlow<List<Task>> = repo.getTasksFlow(taskType).asState(emptyList())

    val shownTasks: StateFlow<List<Task>> = allTasks
        .map { getTasksToShow(if (mode == TaskListMode.SELECT_CATALOG) getOpenGroups() else it) }
        .asState(emptyList())

    val levels: StateFlow<Map<Long, Int>> = allTasks
        .map { tasks -> tasks.associateBy({ it.id }, { level(it) }) }
        .asState(emptyMap())

    private val _selectedItems = MutableStateFlow(listOf<Int>())
    val selectedItems = _selectedItems.asStateFlow()

    private val currentTask: StateFlow<Task?> = _selectedItems
        .map { if (it.count() == 1) getTask(it.first()) else null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val actionModeTitle: StateFlow<String?> = _selectedItems
        .map { currentTask.value?.name }
        .asState(null)

    val showAddButton: StateFlow<Boolean> = getEvents()
        .filterIsInstance<Event.ShowActionMode>()
        .map { !it.show && mode.showAddBtn }
        .asState(mode.showAddBtn)

    val showDoneActionMenu: StateFlow<Boolean> = _selectedItems
        .map { mapToShowDoneActionMenu(it) }
        .asState(false)

    val showEditActionMenu: StateFlow<Boolean> = _selectedItems
        .map { !actionMode.value || it.count() == 1 }
        .asState(false)

    val enabledConfirmMenu: StateFlow<Boolean> = _selectedItems
        .map { mapToEnabledConfirmMenu() }
        .asState(false)

    private val actionMode: StateFlow<Boolean> = _selectedItems
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Variables others */

    val currentTaskID: Long
        get() = currentTask.value?.id ?: -1

    /** =========================================== FUNCTIONS ==================================================== */

    /** Transformations flow */

    private fun mapToShowDoneActionMenu(selected: List<Int>): Boolean {
        val noMultiSelect = selected.count() == 1
        val noGroup = !(currentTask.value?.group ?: false)
        return !actionMode.value || (taskType == TypeTask.SINGLE_TASK && noGroup && noMultiSelect)
    }

    private fun mapToEnabledConfirmMenu() = when (val task = currentTask.value) {
        is Task -> isMarkTaskForSelection(task)
        else -> false
    }

    /** Clicks */

    fun onAddClicked() =
        setEvent(Event.NavigateToAddEdit())

    fun onEditClicked() {
        setEvent(Event.NavigateToAddEdit(currentTask.value))
        destroyActionMode()
    }

    fun onDeleteClicked() {
        _selectedItems.value
            .map { getTask(it) ?: Task() }
            .delete()
        destroyActionMode()
    }

    fun onItemClicked(task: Task) {
        when {
            actionMode.value -> selectItemActionMode(task)
            isMarkTaskForSelection(task) -> selectTaskForSelectionMode(task)
            task.group -> setGroupOpenClose(task)
        }
    }

    fun onItemLongClicked(task: Task) =
        if (mode.supportLongClick) {
            if (!actionMode.value) {
                setActionMode()
            }
            selectItemActionMode(task)
            true
        } else {
            false
        }

    /** ActionMode */

    private fun selectItemActionMode(task: Task) {
        val listAfter = { list: List<Int>, pos: Int -> if (list.contains(pos)) list - pos else list + pos }
        _selectedItems.value = listAfter(_selectedItems.value, task.position())
        if (_selectedItems.value.isEmpty()) {
            destroyActionMode()
        }
    }

    private fun setActionMode() =
        setEvent(Event.ShowActionMode(true))

    fun destroyActionMode() {
        _selectedItems.value = emptyList()
        setEvent(Event.ShowActionMode(false))
    }

    /** ======================================================================================================== */

    private fun isMarkTaskForSelection(task: Task): Boolean =
        (mode == TaskListMode.SELECT_CATALOG) || (mode == TaskListMode.SELECT_TASK && !task.group)

    private fun selectTaskForSelectionMode(task: Task) =
        _selectedItems.apply { value = listOf(task.position()) }

    private fun setGroupOpenClose(task: Task) = task
        .apply { groupOpen = !groupOpen }
        .update()

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

    private fun Task.position() = shownTasks.value.indexOf(this)
    private fun task(id: Long) = allTasks.value.find { it.id == id }
    private fun getTask(index: Int): Task? = shownTasks.value.getOrNull(index)

    private fun level(task: Task): Int =
        generateSequence(task) { task(it.parent) }.count() - 1

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun List<Task>.delete() = viewModelScope.launch { repo.deleteTasks(this@delete) }

    private fun getOpenGroups(): List<Task> =
        allTasks.value.filter { it.group }.map { it.copy(groupOpen = true) }

    private fun <T> Flow<T>.asState(default: T) =
        stateIn(viewModelScope, SharingStarted.Lazily, default)

}