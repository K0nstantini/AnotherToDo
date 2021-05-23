package com.homemade.anothertodo.single_tasks.list

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.homemade.anothertodo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleTaskListFragment : Fragment(R.layout.fragment_single_task_list) {
    private val viewModel: SingleTaskListViewModel by viewModels()


}