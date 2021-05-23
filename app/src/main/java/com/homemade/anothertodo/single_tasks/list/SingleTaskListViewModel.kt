package com.homemade.anothertodo.single_tasks.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.homemade.anothertodo.Repository
import com.homemade.anothertodo.db.entity.SingleTask
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleTaskListViewModel @Inject constructor (private val repo: Repository) : ViewModel() {

    val tasks: LiveData<List<SingleTask>> = repo.singleTasks.asLiveData()

}