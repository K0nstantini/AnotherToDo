package com.homemade.anothertodo.single_tasks.add_edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.homemade.anothertodo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddSingleTaskViewModel @Inject constructor(
    private val repo: Repository,
    val handle: SavedStateHandle
) : ViewModel() {

    val taskName = MutableLiveData<String>()

}