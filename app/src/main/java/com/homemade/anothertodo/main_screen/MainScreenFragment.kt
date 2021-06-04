package com.homemade.anothertodo.main_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentMainScreenBinding
import com.homemade.anothertodo.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainScreenFragment : Fragment(R.layout.fragment_main_screen) {

    private val binding by viewBinding(FragmentMainScreenBinding::bind)
    private val viewModel: MainScreenViewModel by viewModels()

    private val adapter: MainScreenAdapter = MainScreenAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerview.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        viewModel.initData()
    }
}