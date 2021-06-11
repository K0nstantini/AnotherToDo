package com.homemade.anothertodo.statistics

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.homemade.anothertodo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repo: Repository) : ViewModel() {

    private val settingsLive = repo.settingsFlow.asLiveData()

    val countsPointsText = Transformations.map(settingsLive) { set ->
        set?.singleTask?.points?.toString() ?: "No data"
    }
}