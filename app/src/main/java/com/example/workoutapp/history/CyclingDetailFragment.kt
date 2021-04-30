package com.example.workoutapp.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapp.R
import com.example.workoutapp.database.TrackerDao
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.databinding.FragmentCyclingDetailBinding
import com.example.workoutapp.service.calculateTotalDistance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CyclingDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CyclingDetailFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCyclingDetailBinding? = null
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
        // Inflate the layout for this fragment
        _binding = FragmentCyclingDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val args = CyclingDetailFragmentArgs.fromBundle(requireArguments())

        val trackerDao: TrackerDao =
                TrainingDatabase.getDatabase(requireContext().applicationContext).trackerDao

        trackerDao.getCyclingById(args.id).observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->
            val dateText = view.findViewById<TextView>(R.id.dateCyclingDetailText)
            val timeStartText = view.findViewById<TextView>(R.id.timeStartCyclingDetailText)
            val timeEndText = view.findViewById<TextView>(R.id.timeEndCyclingDetailText)
            val totalDistText = view.findViewById<TextView>(R.id.totalDistCyclingDetailText)
            if (data != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = data.cycling.date
                dateText.text = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                val tempCalendar: Calendar = Calendar.getInstance()
                tempCalendar.timeInMillis = data.cycling.timeStart
                timeStartText.text = "${tempCalendar.get(Calendar.HOUR_OF_DAY)}:${tempCalendar.get(Calendar.MINUTE)}:${tempCalendar.get(Calendar.SECOND)}"
                tempCalendar.timeInMillis = data.cycling.timeEnd
                timeEndText.text = "${tempCalendar.get(Calendar.HOUR_OF_DAY)}:${tempCalendar.get(Calendar.MINUTE)}:${tempCalendar.get(Calendar.SECOND)}"
                totalDistText.text = calculateTotalDistance(data).toString()
            } else {
                dateText.text = "Data not found"
                timeStartText.text = "Data not found"
                timeEndText.text = "Data not found"
                totalDistText.text = "Data not found"
            }
        })


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CyclingDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CyclingDetailFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
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

        Log.d("Tracker Result Fragment", listLatLng.toString())
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null)
        _polyline = mMap.addPolyline(PolylineOptions()
                .addAll(listLatLng))
    }
}