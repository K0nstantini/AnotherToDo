package com.homemade.anothertodo.single_tasks.add_edit

import android.app.Application
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
import com.homemade.anothertodo.databinding.FragmentAddSingleTaskBinding
import com.homemade.anothertodo.settingItem.SettingItem
import com.homemade.anothertodo.settingItem.SettingsAdapter
import com.homemade.anothertodo.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

const val SINGLE_TASK_KEY = "singleTaskKey"

@AndroidEntryPoint
class AddSingleTaskFragment : Fragment(R.layout.fragment_add_single_task) {

    private val binding by viewBinding(FragmentAddSingleTaskBinding::bind)
    private val viewModel: AddSingleTaskViewModel by viewModels()

    private lateinit var mainActivity: FragmentActivity
    private lateinit var application: Application

    private val adapter: SettingsAdapter = SettingsAdapter()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, bundle)

        val taskKey = AddSingleTaskFragmentArgs.fromBundle(requireArguments()).singleTaskKey

        mainActivity = requireNotNull(this.activity)
        application = mainActivity.application

        setCloseIcon(mainActivity)

        binding.recyclerview.adapter = adapter

        setObserve()
        setListeners()

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }

    private fun setObserve() = viewModel.apply {
        taskName.observe(viewLifecycleOwner, {
            mainActivity.invalidateOptionsMenu()
        })
        settings.observe(viewLifecycleOwner, {
            it?.let { adapter.data = it }
        })
        group.observe(viewLifecycleOwner, {(set, value) ->
            set?.setStateSwitch(value)
            adapter.notifyDataSetChanged()
        })
        parent.observe(viewLifecycleOwner, { (set, value) ->
            val text = when (value) {
                null -> getString(R.string.settings_main_catalog_text)
                else -> value.name
            }
            set?.setValue(text)
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

        navigateToBack.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { findNavController().popBackStack() }
        })
    }

    private fun setListeners() {
        adapter.setOnClickListener(object : SettingsAdapter.ClickListener {
            override fun onClick(settingItem: SettingItem) = settingItem.action?.invoke()
        })
    }

    private fun setCloseIcon(mainActivity: FragmentActivity) {
        mainActivity.findViewById<Toolbar>(R.id.topAppBar).setNavigationIcon(R.drawable.ic_close)
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