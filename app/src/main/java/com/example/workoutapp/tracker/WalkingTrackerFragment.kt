package com.example.workoutapp.tracker

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapp.R
import com.example.workoutapp.databinding.FragmentWalkingTrackerBinding
import com.example.workoutapp.service.LocationService
import com.example.workoutapp.service.SharedPreferenceUtil
import com.example.workoutapp.service.WalkingService
import com.example.workoutapp.service.toText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalkingTrackerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalkingTrackerFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentWalkingTrackerBinding? = null
    private val binding get() = _binding!!

    private var walkingServiceBound = false
    private var walkingService: WalkingService? = null

    private lateinit var walkingBroadcastReceiver: WalkingBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences

    private val walkingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WalkingService.LocalBinder
            walkingService = binder.service
            walkingServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            walkingService = null
            walkingServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkingTrackerBinding.inflate(inflater, container, false)

        walkingBroadcastReceiver = WalkingBroadcastReceiver()

        sharedPreferences =
                requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        binding.btnTracking.setOnClickListener {
            val enabled = sharedPreferences.getBoolean(
                    getKeyEnabled(), false
            )

            Log.d("Walking Fragment", enabled.toString())
            Log.d("Walking Fragment", walkingService.toString())
            if(enabled){
                walkingService?.unsubscribeToWalkingUpdates()
                        ?: Log.d("Tracker Fragment", "Service not bound")
//                findNavController().navigate(CyclingTrackerFragmentDirections.actionCyclingPageToTrackerResultFragment())
            }else{
                walkingService?.subscribeToWalkingUpdates()
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateButtonState(
                sharedPreferences.getBoolean(getKeyEnabled(), false)
        )

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val walkingServiceIntent = Intent(requireActivity(), WalkingService::class.java)
        requireActivity().bindService(walkingServiceIntent, walkingServiceConnection, Context.BIND_AUTO_CREATE)

        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                walkingBroadcastReceiver,
                IntentFilter(
                        WalkingService.ACTION_FOREGROUND_ONLY_WALKING_BROADCAST
                )
        )

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(
                walkingBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if(walkingServiceBound){
            requireActivity().unbindService(walkingServiceConnection)
            walkingServiceBound = false
        }

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateButtonState(trackingLocation: Boolean){
        if(trackingLocation){
            binding.btnTracking.text = "Stop receiving location"
        }else{
            binding.btnTracking.text = "Start receiving location"
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == getKeyEnabled()){
            updateButtonState(sharedPreferences!!.getBoolean(
                    getKeyEnabled(), false
            ))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WalkingTrackerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            WalkingTrackerFragment()
    }

    private fun getKeyEnabled(): String{
        return SharedPreferenceUtil.KEY_FOREGROUND_ENABLED_WALKING
    }

    private fun setupToolbar(){
        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar
                .setupWithNavController(navController, appBarConfiguration)
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.tvStep.text}"
        binding.tvStep.text = outputWithPreviousLogs
    }


    private inner class WalkingBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val step = intent.getIntExtra(
                    WalkingService.EXTRA_WALKING, 0
            )

            logResultsToScreen("Foreground steps: $step")
        }
    }
}