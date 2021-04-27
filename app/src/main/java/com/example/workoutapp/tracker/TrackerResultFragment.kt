package com.example.workoutapp.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapp.R
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.databinding.FragmentTrackerResultBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.SupportMapFragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackerResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackerResultFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentTrackerResultBinding? = null
    private val binding get() = _binding!!

    private var _mMap: GoogleMap? = null
    private val mMap get() = _mMap!!

    private var _polyline: Polyline? = null
    private val polyline get() = _polyline!!

    private var listLatLng: List<LatLng> = listOf()

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
        _binding = FragmentTrackerResultBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.maps_view) as SupportMapFragment?

        val dataSource = TrainingDatabase.getDatabase(requireContext().applicationContext).trackerDao
        val viewModelFactory = TrackerViewModelFactory(dataSource)
        val trackerViewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TrackerViewModel::class.java)

        trackerViewModel.tracks.observe(viewLifecycleOwner, Observer {
            listLatLng = it.tracks.map {
                LatLng(it.latitude, it.longitude)
            }
        })

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar(){
        val navController = findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolbar
            .setupWithNavController(navController, appBarConfiguration)
    }

    override fun onMapReady(p0: GoogleMap?) {
        _mMap = p0;
        val googlePlex = CameraPosition.Builder()
            .target(LatLng(22.7739, 71.6673))
            .zoom(7f)
            .bearing(0f)
            .tilt(45f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null)
        _polyline = mMap.addPolyline(PolylineOptions()
            .addAll(listLatLng))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrackerResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                TrackerResultFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}