package com.example.workoutapp.tracker

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
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
import com.example.workoutapp.BuildConfig
import com.example.workoutapp.MainActivity
import com.example.workoutapp.R
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.databinding.FragmentTrackerBinding
import com.example.workoutapp.service.LocationService
import com.example.workoutapp.service.SharedPreferenceUtil
import com.example.workoutapp.service.calculateTotalDistance
import com.example.workoutapp.service.toText
import com.google.android.material.snackbar.Snackbar
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
class TrackerFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    private var locationServiceBound = false
    private var locationService: LocationService? = null

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var trackingButton: Button

    private lateinit var outputTextView: TextView

    private val locationServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        val view = binding.root

        val dataSource = TrainingDatabase.getDatabase(requireContext().applicationContext).trackerDao
        val viewModelFactory = TrackerViewModelFactory(dataSource)
        val trackerViewModel = ViewModelProvider(
                this, viewModelFactory
        ).get(TrackerViewModel::class.java)

        trackerViewModel.tracks.observe(viewLifecycleOwner, Observer {
            if(it == null){
                binding.tvDistance.text = "0 KM"
            }else{
                binding.tvDistance.text = calculateTotalDistance(it).toString() + " KM"
            }
        })

        locationBroadcastReceiver = LocationBroadcastReceiver()

        sharedPreferences =
                requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        trackingButton = binding.btnStart
        outputTextView = binding.outputTextView

        trackingButton.setOnClickListener {
            val enabled = sharedPreferences.getBoolean(
                    SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
            )

            if(enabled){
                locationService?.unsubscribeToLocationUpdates()
                        ?: Log.d("Tracker Fragment", "Service not bound")
            }else{
                requestForegroundPermission()
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val formatted = current.format(formatter)

        updateButtonState(
                sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)
        requireActivity().bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
                locationBroadcastReceiver,
                IntentFilter(
                        LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
                )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(
                locationBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if(locationServiceBound){
            requireActivity().unbindService(locationServiceConnection)
            locationServiceBound = false
        }

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED){
            updateButtonState(sharedPreferences!!.getBoolean(
                    SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
            ))
        }
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

    private fun foregroundPermissionApproved(): Boolean{
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestForegroundPermission(){
        val provideRationale = foregroundPermissionApproved()

        Log.d("Tracker Fragment", provideRationale.toString())
        if(provideRationale){
            Snackbar.make(
                    activity!!.findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_LONG
            )
                    .setAction("OK") {
                        // Request permission
                        requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                        )
                    }
                    .show()

        }else{
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d("Tracker Fragment", "onRequestPermissionResult")

        when(requestCode){
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when{
                grantResults.isEmpty() ->
                    Log.d("Tracker Fragment", "User Interaction was cancelled")

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    locationService?.subscribeToLocationUpdates()

                else -> {
                    updateButtonState(false)
                    Snackbar.make(
                            requireActivity().findViewById(R.id.activity_main),
                            "Permission was denied, but is needed for core functionality",
                            Snackbar.LENGTH_LONG
                    )
                            .setAction("Settings"){
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package",
                                        BuildConfig.APPLICATION_ID,
                                        null
                                )

                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .show()
                }

            }
        }
    }

    private fun updateButtonState(trackingLocation: Boolean){
        if(trackingLocation){
            trackingButton.text = "Stop receiving location"
        }else{
            trackingButton.text = "Start receiving location"
        }
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${outputTextView.text}"
        outputTextView.text = outputWithPreviousLogs
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                    LocationService.EXTRA_LOCATION
            )

            if (location != null) {
                logResultsToScreen("Foreground location: ${location.toText()}")
            }
        }
    }
}