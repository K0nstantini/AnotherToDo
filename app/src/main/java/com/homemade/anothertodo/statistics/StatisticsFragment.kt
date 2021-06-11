package com.homemade.anothertodo.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentStatisticsBinding
import com.homemade.anothertodo.utils.delegates.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val binding by viewBinding(FragmentStatisticsBinding::bind)
    private val viewModel: StatisticsViewModel by viewModels()
    private val mainActivity: FragmentActivity by lazy { getMActivity() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setObserve()
    }

    private fun getMActivity() = requireNotNull(this.activity)

    private fun setObserve() = with(viewModel){
        countsPointsText.observe(viewLifecycleOwner) {binding}
    }

}