package com.homemade.anothertodo.main_screen

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentMainScreenBinding
import com.homemade.anothertodo.utils.PrimaryActionModeCallback
import com.homemade.anothertodo.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreenFragment : Fragment(R.layout.fragment_main_screen) {

    private val binding by viewBinding(FragmentMainScreenBinding::bind)
    private val viewModel: MainScreenViewModel by viewModels()
    private val mainActivity: FragmentActivity by lazy { getMActivity() }

    private val adapter: MainScreenAdapter = MainScreenAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter

        setObserve()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        viewModel.initData()
    }

    private fun getMActivity() = requireNotNull(this.activity)

    private fun setObserve() = viewModel.apply {
        singleTasks.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })
        showActionMode.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { setActionMode(it) }
        })
    }

    private fun setListeners() {
        adapter.setOnClickListener { viewModel.onItemClicked(it) }
    }

    private fun setActionMode(callBack: PrimaryActionModeCallback) {
        view?.let {
            callBack.startActionMode(it, R.menu.main_screen_s_task_contextual_action_bar)
        }
        setActionModeListeners(callBack)
    }

    private fun setActionModeListeners(callBack: PrimaryActionModeCallback) {
        val onClick: (MenuItem) -> Unit = { item ->
            when (item.itemId) {
                R.id.menu_done -> viewModel.onDoneClicked()
            }
        }

        callBack.onActionItemClickListener =
            PrimaryActionModeCallback.OnActionItemClickListener { onClick(it) }
        callBack.destroyListener =
            PrimaryActionModeCallback.DestroyListener { viewModel.destroyActionMode() }
    }
}