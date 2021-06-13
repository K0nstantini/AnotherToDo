package com.homemade.anothertodo.single_task

import androidx.lifecycle.*
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.add_classes.MyCalendar
import com.homemade.anothertodo.db.entity.DEFAULT_DEADLINE_SINGLE_TASK
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.dialogs.MyInputDialog
import com.homemade.anothertodo.settingItem.SettingItem
import com.homemade.anothertodo.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SINGLE_TASK_KEY = "taskKey"

@HiltViewModel
class SingleTaskViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel() {

    enum class Sets {
        PARENT,
        GROUP,
        DATE_START,   // Дата, начиная с которой, задача становиться активной
        DEADLINE,
    }

    private val _settings = MutableLiveData<List<SettingItem>>()
    val settings: LiveData<List<SettingItem>> get() = _settings

    private val currentTask = handle.get<Task>(SINGLE_TASK_KEY) ?: Task()

    val taskName = MutableLiveData(currentTask.name)

    private val _group = MutableLiveData(currentTask.group)
    val group = Transformations.map(_group) {
        _settings.value?.get(Sets.GROUP.ordinal) to (it ?: false)
    }

    private val _parent = MutableLiveData(currentTask.parent)
    val parent = Transformations.switchMap(_parent) {
        liveData {
            emit(
                _settings.value?.get(Sets.PARENT.ordinal) to (repo.getTask(it))
            )
        }
    }

    private val dateToday = MyCalendar().today()
    private val _dateStart = MutableLiveData(dateToday)
    val dateStart = Transformations.map(_dateStart) {
        _settings.value?.get(Sets.DATE_START.ordinal) to (it ?: dateToday)
    }

    private val _deadline = MutableLiveData(currentTask.single.deadline)
    val deadline = Transformations.map(_deadline) {
        _settings.value?.get(Sets.DEADLINE.ordinal) to (it ?: 0)
    }

    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>> get() = _navigateToBack

    private val _navigateToParent = MutableLiveData<Event<Boolean>>()
    val navigateToParent: LiveData<Event<Boolean>> get() = _navigateToParent

    init {
        val list = mutableListOf<SettingItem>()
        list.add(
            Sets.PARENT.ordinal,
            SettingItem(R.string.settings_add_task_title_parent)
                .setClear(::onParentClearClicked)
                .setAction(::onParentClicked)
        )
        list.add(
            Sets.GROUP.ordinal,
            SettingItem(R.string.settings_add_task_title_group)
                .setSwitch(::onGroupClicked)
                .setAction(::onGroupClicked)
        )
        list.add(
            Sets.DATE_START.ordinal,
            SettingItem(R.string.settings_add_single_task_title_date_start)
                .setAction(::onsDateStartClicked)
        )
        list.add(
            Sets.DEADLINE.ordinal,
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

    fun setParent(id: Long) = _parent.apply { value = id }

    private fun onGroupClicked() {
        _group.value = !(_group.value ?: false)
        setEnabledSettings()
    }

    private fun onsDateStartClicked() {
        // TODO
    }

    private fun onDeadlineClicked() {
        val timeDeadline = when (_deadline.value ?: 0) {
            0 -> DEFAULT_DEADLINE_SINGLE_TASK
            else -> _deadline.value
        }.toString()

        val dialog = MyInputDialog(::saveDeadline, timeDeadline)
            .setTitle(R.string.alert_title_add_single_task_deadline)
            .setLength(2)
        setInputDialog(dialog)
    }

    fun onSaveClicked(): Boolean {
        // TODO: Проверить заполнение
        saveTask()
        _navigateToBack.value = Event(true)
        return true
    }

    private fun saveTask() {
        currentTask.setData(taskName, _group, _parent, _dateStart, _deadline)
        when (currentTask.id) {
            0L -> currentTask.insert()
            else -> currentTask.update()
        }
    }

    private fun saveDeadline(value: String) {
        _deadline.value = value.toIntOrNull() ?: 0
    }

    private fun setEnabledSettings() {
        val noGroup = !(_group.value ?: false)
        _settings.value?.let { set ->
            set[Sets.DATE_START.ordinal].setEnabled(noGroup)
            set[Sets.DEADLINE.ordinal].setEnabled(noGroup)
        }
    }

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun Task.insert() = viewModelScope.launch { repo.insertTask(this@insert) }

}


/**
var dateUntilToDo: MyCalendar = MyCalendar(),           // Задача должна быть сгенерирована до этой даты
var toDoAfterTask: String = ""                          // Задача будет сегенрирована только после выполнения другой задачи
 **/