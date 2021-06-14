package com.homemade.anothertodo.task_list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentTaskListBinding
import com.homemade.anothertodo.db.entity.Task
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint

const val SELECTED_TASK_ID = "selectedTaskIDKey"

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val binding by viewBinding(FragmentTaskListBinding::bind)
    private val viewModel: TaskListViewModel by viewModels()
    private val mainActivity: FragmentActivity by lazy { getMActivity() }

    private val adapter: TaskAdapter = TaskAdapter()

    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter

        setTitle()
        setObserve()
        setListeners()
    }

    private fun getMActivity() = requireNotNull(this.activity)

    private fun setTitle() {
        (mainActivity as AppCompatActivity).supportActionBar?.title = getString(viewModel.title)
    }

    private fun setObserve() = viewModel.apply {
        // Отображение задач в recyclerview
        shownTasks.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        // Установить уровни иерархии в адаптере recyclerview
        levels.observe(viewLifecycleOwner, {
            adapter.setLevels(it)
        })
        selectedItems.observe(viewLifecycleOwner, {
            it?.let { adapter.setSelections(it) }
        })
        showActionMode.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                actionMode = view?.startActionMode(getCallbackActionMode())
            }
        })
        actionModeTitle.observe(viewLifecycleOwner) {
            actionMode?.title = it
        }
        hideActionMode.observe(viewLifecycleOwner, {
            it?.let { actionMode?.finish() }
        })
        showDoneActionMenu.observe(viewLifecycleOwner) { show ->
            actionMode?.menu?.findItem(R.id.menu_done)?.let { it.isVisible = show }
        }
        showEditActionMenu.observe(viewLifecycleOwner) { show ->
            actionMode?.menu?.findItem(R.id.menu_edit)?.let { it.isVisible = show }
        }
        enabledConfirmMenu.observe(viewLifecycleOwner, {
            it?.let { mainActivity.invalidateOptionsMenu() }
        })
        navigateToAdd.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { navigateToAddEdit(null) }
        })
        navigateToEdit.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { navigateToAddEdit(it) }
        })
    }

    private fun setListeners() {
        adapter.setOnClickListener { viewModel.onItemClicked(it) }
        adapter.setOnLongClickListener { viewModel.onItemLongClicked(it) }
    }

    private fun getCallbackActionMode() = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.task_list_contextual_action_bar, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_delete -> viewModel.onDeleteClicked()
                R.id.menu_edit -> viewModel.onEditClicked()
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) = viewModel.destroyActionMode()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.confirm)?.let {
            it.isEnabled = when (viewModel.enabledConfirmMenu.value) {
                true -> {
                    it.setIcon(R.drawable.ic_confirm_change)
                    true
                }
                else -> {
                    it.setIcon(R.drawable.ic_confirm_change_is_not_enabled)
                    false
                }
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.mode.menu?.let {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(it, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.confirm -> backToParent()
        else -> super.onOptionsItemSelected(item)
    }

    private fun backToParent(): Boolean {
        val cont = findNavController()
        cont.previousBackStackEntry?.savedStateHandle?.set(
            SELECTED_TASK_ID,
            viewModel.currentTaskID
        )
        cont.popBackStack()
        return true
    }

    private fun navigateToAddEdit(task: Task?) {
        findNavController().navigate(
            when (viewModel.taskType) {
                TypeTask.REGULAR_TASK -> TaskListFragmentDirections.actionTaskListFragmentToRegularTaskFragment(task)
                TypeTask.SINGLE_TASK -> TaskListFragmentDirections.actionTaskListFragmentToSingleTaskFragment(task)
            }
        )
    }


}