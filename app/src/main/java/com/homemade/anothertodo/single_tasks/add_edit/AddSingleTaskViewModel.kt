package com.homemade.anothertodo.single_tasks.add_edit

import androidx.lifecycle.*
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.db.entity.DEFAULT_DEADLINE_SINGLE_TASK
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.settingItem.SettingItem
import com.homemade.anothertodo.single_tasks.list.SELECTED_SINGLE_TASK_ID
import com.homemade.anothertodo.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SINGLE_TASK_KEY = "singleTaskKey"

@HiltViewModel
class AddSingleTaskViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : ViewModel() {

    enum class SettingsAddSingleTasks {
        PARENT,
        GROUP,
        DATE_START,   // Дата, начиная с которой, задача становиться активной
        DEADLINE,
    }

    private val _settings = MutableLiveData<List<SettingItem>>()
    val settings: LiveData<List<SettingItem>> get() = _settings

    private val currentTask = handle.get<SingleTask>(SINGLE_TASK_KEY) ?: SingleTask()

    val taskName = MutableLiveData(currentTask.name)

    private val _group = MutableLiveData(currentTask.group)
    val group = Transformations.map(_group) {
        _settings.value?.get(SettingsAddSingleTasks.GROUP.ordinal) to (it ?: false)
    }

    private val _parent = MutableLiveData(currentTask.parent)
    val parent = Transformations.switchMap(_parent) {
        liveData {
            emit(
                _settings.value?.get(SettingsAddSingleTasks.PARENT.ordinal) to (repo.getTask(it))
            )
        }
    }

    private val dateToday = MyCalendar().today()
    private val _dateStart = MutableLiveData(dateToday)
    val dateStart = Transformations.map(_dateStart) {
        _settings.value?.get(SettingsAddSingleTasks.DATE_START.ordinal) to (it ?: dateToday)
    }

    private val _deadline = MutableLiveData(DEFAULT_DEADLINE_SINGLE_TASK)
    val deadline = Transformations.map(_deadline) {
        _settings.value?.get(SettingsAddSingleTasks.DEADLINE.ordinal) to (it ?: 0)
    }

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>> get() = _navigateToBack

    private val _navigateToParent = MutableLiveData<Event<Boolean>>()
    val navigateToParent: LiveData<Event<Boolean>> get() = _navigateToParent

    init {
        val list = mutableListOf<SettingItem>()
        list.add(
            SettingsAddSingleTasks.PARENT.ordinal,
            SettingItem(R.string.settings_add_single_task_title_parent)
                .setClear(::onParentClearClicked)
                .setAction(::onParentClicked)
        )
        list.add(
            SettingsAddSingleTasks.GROUP.ordinal,
            SettingItem(R.string.settings_add_single_task_title_group)
                .setSwitch(::onGroupClicked)
                .setAction(::onGroupClicked)
        )
        list.add(
            SettingsAddSingleTasks.DATE_START.ordinal,
            SettingItem(R.string.settings_add_single_task_title_date_start)
                .setAction(::onsDateStartClicked)
        )
        list.add(
            SettingsAddSingleTasks.DEADLINE.ordinal,
            SettingItem(R.string.settings_add_single_task_title_deadline)
                .setAction(::onDeadlineClicked)
        )

        _settings.value = list

        setEnabledSettings()
    }

    private fun onParentClicked() {
        _navigateToParent.value = Event(true)
    }

    private fun onParentClearClicked() {
        _parent.value = 0
    }

    fun setParent(id: Long) {
        _parent.value = id
    }

    private fun onGroupClicked() {
        _group.value = !(_group.value ?: false)
        setEnabledSettings()
    }

    private fun onsDateStartClicked() {
        // TODO
    }

    private fun onDeadlineClicked() {
        // TODO
    }

    fun onSaveClicked(): Boolean {
        // TODO: Проверить заполнение
        saveTask()
        _navigateToBack.value = Event(true)
        return true
    }

    private fun saveTask() = viewModelScope.launch {
        currentTask.setData(taskName, _group, _parent, _dateStart, _deadline)
        repo.insertSingleTask(currentTask) // FIXME: add update
    }

    private fun setEnabledSettings() {
        val noGroup = !(_group.value ?: false)
        _settings.value?.let { set ->
            set[SettingsAddSingleTasks.DATE_START.ordinal].setEnabled(noGroup)
            set[SettingsAddSingleTasks.DEADLINE.ordinal].setEnabled(noGroup)
        }
    }

}

/**
var dateUntilToDo: MyCalendar = MyCalendar(),           // Задача должна быть сгенерирована до этой даты
var toDoAfterTask: String = ""                          // Задача будет сегенрирована только после выполнения другой задачи
 **/