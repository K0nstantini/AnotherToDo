package com.homemade.anothertodo.main_screen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentMainScreenBinding
import com.homemade.anothertodo.utils.delegates.viewBinding
import com.homemade.anothertodo.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreenFragment : Fragment(R.layout.fragment_main_screen) {

    private val binding by viewBinding(FragmentMainScreenBinding::bind)
    private val viewModel: MainScreenViewModel by viewModels()
    private val mainActivity: FragmentActivity by lazy { getMActivity() }

    private val adapter: MainScreenAdapter = MainScreenAdapter()

    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter

        setObserve()
        setListeners()
    }

    private fun getMActivity() = requireNotNull(this.activity)

    private fun setObserve() = viewModel.apply {
        settingsLive.observe(viewLifecycleOwner) {
            viewModel.initData()
        }
        shownSingleTasks.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        currentTaskPosition.observe(viewLifecycleOwner, {
            it?.let { adapter.setSelections(it) }
        })
        showActionMode.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                actionMode = view?.startActionMode(getCallbackActionMode())
                actionMode?.title = currentTaskName
            }
        })
        hideActionMode.observe(viewLifecycleOwner, {
            it?.let { actionMode?.finish() }
        })
        showSingleChoiceDialog.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.show(mainActivity)
        })
        showConfirmDialog.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.show(mainActivity)
        })
        message.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { mainActivity.toast(it) }
        })
    }

    private fun setListeners() {
        adapter.setOnClickListener { viewModel.onItemClicked(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_screen, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_postpone_next_task -> viewModel.onPostponeNextTaskClicked()
        else -> super.onOptionsItemSelected(item)
    }

    private fun getCallbackActionMode() = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.main_screen_s_task_contextual_action_bar, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_postpone -> viewModel.onPostponeCurrentTaskClicked()
                R.id.menu_roll -> viewModel.onRollClicked()
                R.id.menu_done -> viewModel.onDoneClicked()
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) = viewModel.destroyActionMode()
    }

}