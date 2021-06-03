package com.homemade.anothertodo.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.add_classes.MyPreference
import com.homemade.anothertodo.single_tasks.setSingleTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repo: Repository,
    private val pref: MyPreference
) : ViewModel() {

    fun initData() = viewModelScope.launch { setSingleTasks(pref, repo) }

}