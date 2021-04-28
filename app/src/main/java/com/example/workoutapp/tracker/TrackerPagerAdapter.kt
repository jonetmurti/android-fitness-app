package com.example.workoutapp.tracker

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter as FragmentStateAdapter1

class TrackerPagerAdapter(fragment: Fragment): FragmentStateAdapter1(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CyclingTrackerFragment.newInstance()
            else -> WalkingTrackerFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

}