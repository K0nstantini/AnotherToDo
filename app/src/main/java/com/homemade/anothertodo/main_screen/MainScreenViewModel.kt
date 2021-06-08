package com.homemade.anothertodo.main_screen

import androidx.lifecycle.*
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.entity.SingleTask
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
    private val pref: MyPreference,
    private val alarmService: AlarmService
) : ViewModel() {

    val singleTasks: LiveData<List<SingleTask>> = repo.singleTasksToDoFlow.asLiveData()

    private val _showActionMode = MutableLiveData<Event<PrimaryActionModeCallback>>()
    val showActionMode: LiveData<Event<PrimaryActionModeCallback>> get() = _showActionMode

    private var actionMode = MutableLiveData<PrimaryActionModeCallback?>(null)
    private val isActionMode: Boolean get() = actionMode.value != null

    private var currentTask: SingleTask? = null
    val currentTaskID: Long get() = currentTask?.id ?: -1

    fun initData() = viewModelScope.launch {
//        delClearData()
//        return@launch
        val tasks = repo.getSingleTasks()
        val lastDateActivation = pref.dateActivationSingleTask
        if (needToActivateSingleTasks(tasks, lastDateActivation)) {

            val dates = getDatesToActivateSingleTasks(
                tasks,
                pref.frequencySingleTasks,
                lastDateActivation
            )

            val lastDate = dates.last()
            alarmService.setExactAlarm(lastDate)
            pref.dateActivationSingleTask = lastDate

            getTasksToUpdateDatesActivation(tasks, dates).update()
        }
    }

    // FIXME: Del
    private suspend fun delClearData() {
        pref.dateActivationSingleTask = MyCalendar()
        repo.getSingleTasks().filter { it.dateActivation.isNoEmpty() }.forEach { task ->
            task.dateActivation = MyCalendar()
            repo.updateSingleTask(task)
        }
    }

    fun onItemClicked(task: SingleTask) {
        TODO("Not yet implemented")
    }

    fun onDoneClicked() {
        TODO("Not yet implemented")
    }

    // FIXME
    fun destroyActionMode() {
        actionMode.value?.finishActionMode()
        actionMode.value = null
//        _selectedItems.value = emptyList()
        currentTask = null
    }

    private fun needToActivateSingleTasks(tasks: List<SingleTask>, date: MyCalendar) =
        date < MyCalendar().now() && tasks.any { it.readyToActivate }


    private suspend fun List<SingleTask>.update() = repo.updateSingleTasks(this)

}