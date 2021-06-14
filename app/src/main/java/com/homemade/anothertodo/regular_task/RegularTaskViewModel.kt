package com.homemade.anothertodo.regular_task

import androidx.lifecycle.*
import com.homemade.anothertodo.R
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.BaseViewModel
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.settingItem.SettingItem
import com.homemade.anothertodo.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val REGULAR_TASK_KEY = "taskKey"

@HiltViewModel
class RegularTaskViewModel @Inject constructor(
    private val repo: Repository,
    handle: SavedStateHandle
) : BaseViewModel() {

    enum class Sets {
        PARENT,
        GROUP,
        CHOOSE_FROM_GROUP,
        FREQUENCY_FROM,
        FREQUENCY_TO,
        PERIOD_GENERATION,
        WORKING_TIME,
        FINISH_DATE,
    }

    private val _settings = MutableLiveData(
        mapOf(
            Sets.PARENT to
                    SettingItem(R.string.settings_add_task_title_parent)
                        .setClear(::onParentClearClicked)
                        .setAction(::onParentClicked),
            Sets.GROUP to
                    SettingItem(R.string.settings_add_task_title_group)
                        .setSwitch(::onGroupClicked)
                        .setAction(::onGroupClicked),
            Sets.CHOOSE_FROM_GROUP to
                    SettingItem(R.string.settings_add_regular_task_choose_from_group_text)
                        .setSwitch(::onChooseFromGroupClicked)
                        .setAction(::onChooseFromGroupClicked),
        )
    )

    val settings = Transformations.map(_settings) { it.values.toList() }

    private val currentTask = handle.get<Task>(REGULAR_TASK_KEY) ?: Task(type = TypeTask.REGULAR_TASK)

    val taskName = MutableLiveData(currentTask.name)

    private val _group = MutableLiveData(currentTask.group)
    val group = Transformations.map(_group) {
        _settings.value?.get(Sets.GROUP)?.setStateSwitch(it ?: false)
    }

    private val _parentID = MutableLiveData(currentTask.parent)
    val parent = Transformations.switchMap(_parentID) {
        liveData { emit(repo.getTask(it)) }
    }
    val parentName = Transformations.map(parent) {
        _settings.value?.get(Sets.PARENT) to it?.name
    }
    val parentClear = Transformations.map(_parentID) { parentID ->
        _settings.value?.get(Sets.PARENT) to (parentID == null)
    }

    /** Navigation */
    private val _navigateToBack = MutableLiveData<Event<Boolean>>()
    val navigateToBack: LiveData<Event<Boolean>> get() = _navigateToBack

    private val _navigateToParent = MutableLiveData<Event<Boolean>>()
    val navigateToParent: LiveData<Event<Boolean>> get() = _navigateToParent

    init {
//        setEnabledSettings()
    }

    private fun onParentClicked() {
        _navigateToParent.value = Event(true)
    }

    private fun onParentClearClicked() {
        _parentID.value = 0
    }

    private fun onGroupClicked() {
        _group.value = !(_group.value ?: false)
//        setEnabledSettings()
    }

    private fun onChooseFromGroupClicked() {
        TODO()
    }

    fun onSaveClicked(): Boolean {
        saveTask()
        _navigateToBack.value = Event(true)
        return true
    }

    private fun saveTask() {
        // TODO: Проверить заполнение
        currentTask
            .setName(taskName)
            .setGroup(_group)
            .setParent(_parentID)
        when (currentTask.id) {
            0L -> currentTask.insert()
            else -> currentTask.update()
        }
    }


    private fun setEnabledSettings() {
        TODO()
//        val noGroup = !(_group.value ?: false)
//        _settings.value?.let { set ->
//            set[SingleTaskViewModel.Sets.DATE_START.ordinal].setEnabled(noGroup)
//            set[SingleTaskViewModel.Sets.DEADLINE.ordinal].setEnabled(noGroup)
//        }
    }

    fun setParent(id: Long) = _parentID.apply { value = id }

    private fun Task.update() = viewModelScope.launch { repo.updateTask(this@update) }
    private fun Task.insert() = viewModelScope.launch { repo.insertTask(this@insert) }


}