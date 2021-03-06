package com.homemade.anothertodo.single_task

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentSingleTaskBinding
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.settingItem.SettingsAdapter
import com.homemade.anothertodo.task_list.SELECTED_TASK_ID
import com.homemade.anothertodo.utils.delegates.viewBinding
import com.homemade.anothertodo.utils.setAppTitle
import com.homemade.anothertodo.utils.setCloseIcon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleTaskFragment : Fragment(R.layout.fragment_single_task) {

    private val binding by viewBinding(FragmentSingleTaskBinding::bind)
    private val viewModel: SingleTaskViewModel by viewModels()
    private val mainActivity: FragmentActivity by lazy { getMActivity() }
    private val adapter: SettingsAdapter = SettingsAdapter()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, bundle)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter

        mainActivity.apply {
            setCloseIcon()
            setAppTitle(viewModel.taskName.value)
        }

        setObserve()
        setListeners()

    }

    private fun getMActivity() = requireNotNull(this.activity)

    private fun setObserve() = viewModel.apply {
        taskName.observe(viewLifecycleOwner, {
            mainActivity.invalidateOptionsMenu()
        })
        settings.observe(viewLifecycleOwner, {
            it?.let { adapter.data = it }
        })
        group.observe(viewLifecycleOwner, { (set, value) ->
            set?.setStateSwitch(value)
            adapter.notifyDataSetChanged()
        })
        parent.observe(viewLifecycleOwner, { (set, value) ->
            set?.setValue(value?.name, getString(R.string.settings_main_catalog_text))
            set?.setShowClear(value != null)
            adapter.notifyDataSetChanged()
        })
        dateStart.observe(viewLifecycleOwner, { (set, value) ->
            set?.setValue(value.toString(false))
            adapter.notifyDataSetChanged()
        })
        deadline.observe(viewLifecycleOwner, { (set, value) ->
            val text = when (value) {
                0 -> getString(R.string.settings_add_single_task_deadline_zero_text)
                else -> getString(R.string.settings_add_single_task_deadline_time_hours_text, value)
            }
            set?.setValue(text)
            adapter.notifyDataSetChanged()
        })

        showInputDialog.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.show(mainActivity)
        })

        navigateToBack.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { findNavController().popBackStack() }
        })
        navigateToParent.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { navigateToParent() }
        })
        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<Long?>(SELECTED_TASK_ID)?.observe(viewLifecycleOwner) { id ->
            setParent(id)
            handle.remove<Long>(SELECTED_TASK_ID)
        }
    }

    private fun setListeners() = adapter.setOnClickListener { it.action?.invoke() }

    private fun navigateToParent() {
        val direction =
            (SingleTaskFragmentDirections)::actionSingleTaskFragmentToTaskListFragment
        val parent = viewModel.parent.value?.second
        findNavController().navigate(direction(TaskListMode.SELECT_CATALOG, TypeTask.SINGLE_TASK, parent))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.save_btn_menu).isEnabled = !viewModel.taskName.value.isNullOrEmpty()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_btn_menu -> viewModel.onSaveClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

}