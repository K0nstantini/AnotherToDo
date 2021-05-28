package com.homemade.anothertodo.single_tasks.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentSingleTaskListBinding
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.DestroyListener
import com.homemade.anothertodo.utils.OnActionItemClickListener
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import com.homemade.anothertodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleTaskListFragment : Fragment(R.layout.fragment_single_task_list) {

    private val binding by viewBinding(FragmentSingleTaskListBinding::bind)
    private val viewModel: SingleTaskListViewModel by viewModels()
    private lateinit var adapter: SingleTaskListAdapter

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = SingleTaskListAdapter()
        binding.recyclerview.adapter = adapter

        setObserve()
        setListeners()
    }

    private fun setObserve() = viewModel.apply {
        tasks.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        selectedItem.observe(viewLifecycleOwner, {
            it?.let { adapter.setSelections(it) }
        })
        showActionMode.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { setActionMode(it) }
        })
        navigateToAdd.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { navigateToAddEdit(null) }
        })
        navigateToEdit.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { navigateToAddEdit(it) }
        })
    }

    private fun setListeners() {
        adapter.setOnClickListener(object : SingleTaskListAdapter.ClickListener {
            override fun onClick(task: SingleTask) = viewModel.onItemClicked(task)
        })
        adapter.setOnLongClickListener(object : SingleTaskListAdapter.LongClickListener {
            override fun onLongClick(task: SingleTask) = viewModel.onItemLongClicked(task)
        })
    }

    private fun setActionMode(callBack: PrimaryActionModeCallback) {
        view?.let {
            callBack.startActionMode(
                it,
                R.menu.contextual_action_bar,
                getString(R.string.title_action_mode)
            )
        }
        setActionModeListeners(callBack)
    }

    private fun setActionModeListeners(callBack: PrimaryActionModeCallback) {
        val onClick: (MenuItem) -> Unit = { item ->
            when (item.itemId) {
                R.id.menu_delete -> viewModel.onDeleteClicked()
                R.id.menu_edit -> viewModel.onEditClicked()
            }
        }

        callBack.onActionItemClickListener = object : OnActionItemClickListener {
            override fun onActionItemClick(item: MenuItem) = onClick(item)
        }

        callBack.destroyListener = object : DestroyListener {
            override fun destroy() = viewModel.destroyActionMode()
        }
    }

    private fun navigateToAddEdit(task: SingleTask?) {
        val action = SingleTaskListFragmentDirections
            .actionSingleTaskListFragmentToAddSingleTaskFragment(task)
        findNavController().navigate(action)
    }
}