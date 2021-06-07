package com.homemade.anothertodo.main_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.alarm.AlarmService
import com.homemade.anothertodo.db.entity.SingleTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: Repository,
    private val pref: MyPreference,
    private val alarmService: AlarmService
) : ViewModel() {

    val singleTasks: LiveData<List<SingleTask>> = repo.singleTasksFlow.asLiveData()

    fun initData() = viewModelScope.launch {
//        setSingleTasks(pref, repo, alarmService)
    }


}