package com.homemade.anothertodo.single_tasks.add_edit

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentAddSingleTaskBinding
import com.homemade.anothertodo.single_tasks.list.SingleTaskListViewModel
import com.homemade.anothertodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddSingleTaskFragment : Fragment(R.layout.fragment_add_single_task) {

    private val binding by viewBinding(FragmentAddSingleTaskBinding::bind)

    private val viewModel: AddSingleTaskViewModel by viewModels()



}