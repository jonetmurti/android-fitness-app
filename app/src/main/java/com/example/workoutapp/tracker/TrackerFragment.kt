package com.example.workoutapp.tracker

import android.Manifest
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.workoutapp.BuildConfig
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.databinding.FragmentTrackerBinding
import com.example.workoutapp.service.*
import com.example.workoutapp.service.SharedPreferenceUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.hours

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

/**
 * A simple [Fragment] subclass.
 * Use the [TrackerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackerFragment : Fragment() {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    lateinit var viewPager: ViewPager2
    lateinit var pagerAdapter: TrackerPagerAdapter

    private val trackerViewModel by lazy {
        val dataSource = TrainingDatabase.getDatabase(requireContext().applicationContext).trackerDao
        val viewModelFactory = TrackerViewModelFactory(requireActivity().application, dataSource)
        ViewModelProvider(
                this, viewModelFactory
        ).get(TrackerViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

        viewPager = binding.mypager

        pagerAdapter = TrackerPagerAdapter(this)
        viewPager.adapter = pagerAdapter

//        val tabLayout = binding.tabLayout
//        TabLayoutMediator(tabLayout, viewPager, )
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar(){
        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar
            .setupWithNavController(navController, appBarConfiguration)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TrackerFragmet.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TrackerFragment()
    }

}