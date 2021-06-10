package com.homemade.anothertodo.main_screen

import androidx.lifecycle.*
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.entity.Settings
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.dialogs.MyConfirmAlertDialog
import com.homemade.anothertodo.single_tasks.getDatesToActivateSingleTasks
import com.homemade.anothertodo.single_tasks.getTasksToUpdateDatesActivation
import com.homemade.anothertodo.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: Repository,
    private val alarmService: AlarmService
) : BaseViewModel() {

    private val settings: Settings = repo.settings

    private val singleTasksLive: LiveData<List<SingleTask>> = repo.singleTasksFlow.asLiveData()
    val shownSingleTasks = Transformations.map(singleTasksLive) { tasks ->
        tasks.filter { it.dateActivation.isNoEmpty() }.sortedBy { it.dateActivation.milli }
    }
    private val singleTasks: List<SingleTask> get() = singleTasksLive.value ?: emptyList()

    private var isActionMode: Boolean = false

    private val _showActionMode = MutableLiveData<Event<Boolean>>()
    val showActionMode: LiveData<Event<Boolean>> get() = _showActionMode

    private val _hideActionMode = MutableLiveData<Event<Boolean>>()
    val hideActionMode: LiveData<Event<Boolean>> get() = _hideActionMode


    private var currentTask = MutableLiveData<SingleTask?>(null)
    val currentTaskName: String get() = currentTask.value?.name ?: String()
    val currentTaskPosition = Transformations.map(currentTask) { it?.position() ?: -1 }

    fun initData() {
        setSingleTasks()
    }

    private fun setSingleTasks() = viewModelScope.launch {
//        delClearData()
//        return@launch
        if (needToActivateSingleTasks(singleTasks, settings.singleTask.dateActivation)) {

            val dates = getDatesToActivateSingleTasks(
                singleTasks,
                settings.singleTask.frequency,
                settings.singleTask.dateActivation
            )

            val lastDate = dates.last()
            alarmService.setExactAlarm(lastDate)
            settings.apply { singleTask.dateActivation = lastDate }.update()

            getTasksToUpdateDatesActivation(singleTasks, dates).update()
        }
    }

    // FIXME: Del
    private suspend fun delClearData() {
        settings.apply { singleTask.dateActivation = MyCalendar() }.update()
        val tasks = repo.getSingleTasks()
        tasks.filter { it.dateActivation.isNoEmpty() }.forEach { task ->
            task.apply { dateActivation = MyCalendar() }.update()
        }
        tasks.filter { it.rolls > 0 }.forEach { task ->
            task.apply { rolls = 0 }.update()
        }
    }

    fun onItemClicked(task: SingleTask) {
        currentTask.value = task
        if (isActionMode) destroyActionMode() else setActionMode()
    }

    fun onPostponeClicked() {
        TODO()
    }

    fun onRollClicked() {
        if (settings.singleTask.points < settings.singleTask.pointsForRoll) {
            setMessage(R.string.message_roll_not_enough_points)
        } else {
            val dialog = MyConfirmAlertDialog(::rollSingleTask)
                .setTitle(currentTaskName)
                .setMessage(R.string.alert_title_single_task_roll)
            setConfirmDialog(dialog)
        }
    }

    fun onDoneClicked() {
        val dialog = MyConfirmAlertDialog(::doneSingleTask)
            .setTitle(currentTaskName)
            .setMessage(R.string.alert_title_single_task_done)
        setConfirmDialog(dialog)
    }

    private fun rollSingleTask() {
        val filteredTasks = { singleTasks.filter { it.readyToActivate } }
        val tasksInRoll = when (settings.singleTask.currentTaskTakePartInRoll) {
            true -> filteredTasks() + currentTask.value
            false -> filteredTasks()
        }
        val newTask = tasksInRoll.shuffled().randomOrNull()
        val oldTask = currentTask.value
        if (newTask == null) {
            setMessage(R.string.message_roll_not_find_task)
        } else if (oldTask != null) {
            newTask.apply { dateActivation = oldTask.dateActivation }.update()
            oldTask.apply {
                dateActivation = MyCalendar()
                rolls++
            }.update()
            settings.addPoints(settings.singleTask.pointsForRoll)
            destroyActionMode()
        }
    }

    private fun doneSingleTask() {
//        currentTask.value?.delete()
        if (settings.singleTask.rewards) {
            settings.addPoints(settings.singleTask.pointsForTask)
        }
        destroyActionMode()
    }

    private fun Settings.addPoints(points: Int) = apply { singleTask.points += points }.update()

    private fun setActionMode() {
        _showActionMode.value = Event(true)
        isActionMode = true
    }

    fun destroyActionMode() {
        if (isActionMode) {
            isActionMode = false
            currentTask.value = null
            _hideActionMode.value = Event(true)
        }
    }

    private fun needToActivateSingleTasks(tasks: List<SingleTask>, date: MyCalendar) =
        date < MyCalendar().now() && tasks.any { it.readyToActivate }

    private fun SingleTask.position() = shownSingleTasks.value?.indexOf(this) ?: -1

    private fun SingleTask.update() =
        viewModelScope.launch { repo.updateSingleTask(this@update) }

    private fun List<SingleTask>.update() =
        viewModelScope.launch { repo.updateSingleTasks(this@update) }

    private fun SingleTask.delete() =
        viewModelScope.launch { repo.deleteSingleTask(this@delete) }

    private fun Settings.update() = viewModelScope.launch { repo.updateSettings(this@update) }
}