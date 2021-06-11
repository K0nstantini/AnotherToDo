package com.homemade.anothertodo.single_tasks.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentSingleTaskListBinding
import com.homemade.anothertodo.db.entity.SingleTask
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import com.homemade.anothertodo.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint

const val SELECTED_SINGLE_TASK_ID = "selectedSingleTaskIDKey"

@AndroidEntryPoint
class SingleTaskListFragment : Fragment(R.layout.fragment_single_task_list) {

    private val binding by viewBinding(FragmentSingleTaskListBinding::bind)
    private val viewModel: SingleTaskListViewModel by viewModels()

    private val mainActivity: FragmentActivity by lazy { getMActivity() }

    private val adapter: SingleTaskListAdapter = SingleTaskListAdapter()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        setHasOptionsMenu(true)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter

        setTitle()
        setObserve()
        setListeners()
    }

    private fun setTitle() {
        (mainActivity as AppCompatActivity).supportActionBar?.title = getString(viewModel.title)
    }

    private fun getMActivity() = requireNotNull(this.activity)

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
            event.getContentIfNotHandled()?.let { setActionMode(it) }
        })
        // Отображение иконки подтверждения выбора каталога
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

    private fun setActionMode(callBack: PrimaryActionModeCallback) {
        view?.let {
            callBack.startActionMode(
                it,
                R.menu.s_task_contextual_action_bar,
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

        callBack.onActionItemClickListener =
            PrimaryActionModeCallback.OnActionItemClickListener { onClick(it) }
        callBack.destroyListener =
            PrimaryActionModeCallback.DestroyListener { viewModel.destroyActionMode() }
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
            SELECTED_SINGLE_TASK_ID,
            viewModel.currentTaskID
        )
        cont.popBackStack()
        return true
    }

    private fun navigateToAddEdit(task: SingleTask?) {
        val action = SingleTaskListFragmentDirections
            .actionSingleTaskListFragmentToAddSingleTaskFragment(task)
        findNavController().navigate(action)
    }
}