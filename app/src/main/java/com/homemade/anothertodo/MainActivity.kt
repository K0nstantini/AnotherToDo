package com.homemade.anothertodo

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.homemade.anothertodo.databinding.ActivityMainBinding
import com.homemade.anothertodo.enums.TaskListMode
import com.homemade.anothertodo.enums.TypeTask
import com.homemade.anothertodo.main_screen.MainScreenFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val binding by lazy { getMainBinding() }
    private val drawerLayout by lazy { getDrawer() }
    private val navController by lazy { getController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        binding.navView.setNavigationItemSelectedListener(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navigation_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    private fun getMainBinding() = ActivityMainBinding.inflate(layoutInflater)
    private fun getDrawer() = binding.drawerLayout
    private fun getController() = this.findNavController(R.id.navigation_fragment)

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.taskListFragment -> navigateToTaskList(TypeTask.REGULAR_TASK)
            R.id.singleTaskListFragment -> navigateToTaskList(TypeTask.SINGLE_TASK)
            R.id.statisticFragment -> navController.navigate(R.id.statisticFragment)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navigateToTaskList(type: TypeTask) = navController.navigate(
        MainScreenFragmentDirections.actionMainScreenFragmentToTaskListFragment(TaskListMode.DEFAULT, type)
    )

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}