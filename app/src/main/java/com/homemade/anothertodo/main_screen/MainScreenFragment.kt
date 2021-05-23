package com.homemade.anothertodo.main_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.homemade.anothertodo.R
import com.homemade.anothertodo.databinding.FragmentMainScreenBinding
import com.homemade.anothertodo.utils.viewBinding

class MainScreenFragment : Fragment(R.layout.fragment_main_screen) {
    private val binding by viewBinding(FragmentMainScreenBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}