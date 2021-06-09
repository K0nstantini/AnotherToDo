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
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: Repository,
    private val alarmService: AlarmService
) : BaseViewModel() {

    private val settings: Settings = repo.settings

    val singleTasks: LiveData<List<SingleTask>> = repo.singleTasksToDoFlow.asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var actionMode = MutableLiveData<PrimaryActionModeCallback?>(null)

    private var currentTask = MutableLiveData<SingleTask?>(null)
    val currentTaskName: String get() = currentTask.value?.name ?: String()
    val currentTaskPosition = Transformations.map(currentTask) { it?.position() ?: -1 }

    fun initData() = viewModelScope.launch {
//        delClearData()
//        return@launch
        val tasks = repo.getSingleTasks()
        if (needToActivateSingleTasks(tasks, settings.singleTask.dateActivation)) {

            val dates = getDatesToActivateSingleTasks(
                tasks,
                settings.singleTask.frequency,
                settings.singleTask.dateActivation
            )

            val lastDate = dates.last()
            alarmService.setExactAlarm(lastDate)
            settings.apply { singleTask.dateActivation = lastDate }.update()

            getTasksToUpdateDatesActivation(tasks, dates).update()
        }
    }

    // FIXME: Del
    private suspend fun delClearData() {
        settings.apply { singleTask.dateActivation = MyCalendar() }.update()
        repo.getSingleTasks().filter { it.dateActivation.isNoEmpty() }.forEach { task ->
            task.dateActivation = MyCalendar()
            repo.updateSingleTask(task)
        }
    }

    fun onItemClicked(task: SingleTask) {
        currentTask.value = task
        if (actionMode.value == null) {
            setActionMode()
        } else {
            destroyActionMode()
        }
    }

    fun onDoneClicked() {
        val dialog = MyConfirmAlertDialog(::deleteSingleTask)
            .setTitle(currentTaskName)
            .setMessage(R.string.alert_title_single_task_done)
        setConfirmDialog(dialog)
    }

    private fun deleteSingleTask() {
//        currentTask.value?.let { deleteSingleTaskFromBase(it) } // FIXME
    }

    private fun setActionMode() {
        val actMode = PrimaryActionModeCallback()
        actionMode.value = actMode
        _showActionMode.value = Event(actMode)
    }

    fun destroyActionMode() {
        actionMode.value?.finishActionMode()
        actionMode.value = null
        currentTask.value = null
    }

    private fun needToActivateSingleTasks(tasks: List<SingleTask>, date: MyCalendar) =
        date < MyCalendar().now() && tasks.any { it.readyToActivate }

    private fun SingleTask.position() = singleTasks.value?.indexOf(this) ?: -1


    private suspend fun List<SingleTask>.update() = repo.updateSingleTasks(this)

    private fun deleteSingleTaskFromBase(task: SingleTask) = viewModelScope.launch {
        repo.deleteSingleTask(task)
    }

    private fun Settings.update() = viewModelScope.launch { repo.updateSettings(this@update) }
}