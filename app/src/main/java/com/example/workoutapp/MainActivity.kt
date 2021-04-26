package com.example.workoutapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.example.workoutapp.databinding.ActivityMainBinding
import com.example.workoutapp.news.NewsFragment
import com.example.workoutapp.scheduler.SchedulerFragment
import com.example.workoutapp.tracker.TrackerFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.news_page -> {
                val newsFragment = NewsFragment.newInstance()
                openFragment(newsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.tracker_page -> {
                val trackerFragment = TrackerFragment.newInstance()
                openFragment(trackerFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.history_page -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.scheduler_page -> {
                val schedulerFragment = SchedulerFragment.newInstance()
                openFragment(schedulerFragment)
                return@OnNavigationItemSelectedListener true
            }
            else -> return@OnNavigationItemSelectedListener false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
//        binding.bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}