package com.example.workoutapp.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapp.R
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.database.Walking
import com.example.workoutapp.database.WalkingDao
import com.example.workoutapp.databinding.FragmentWalkingDetailBinding
import kotlinx.coroutines.runBlocking
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalkingDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalkingDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentWalkingDetailBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentWalkingDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        val args = WalkingDetailFragmentArgs.fromBundle(requireArguments())

        val walkingDao: WalkingDao =
                TrainingDatabase.getDatabase(requireContext().applicationContext).walkingDao

        walkingDao.getWalkingById(args.id).observe(viewLifecycleOwner, androidx.lifecycle.Observer { walking ->
            val dateText = view.findViewById<TextView>(R.id.dateWalkDetailText)
            val timeStartText = view.findViewById<TextView>(R.id.timeStartWalkDetailText)
            val timeEndText = view.findViewById<TextView>(R.id.timeEndWalkDetailText)
            val totalStepText = view.findViewById<TextView>(R.id.totalStepWalkDetailText)
            if (walking != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = walking.date
                dateText.text = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                val tempCalendar: Calendar = Calendar.getInstance()
                tempCalendar.timeInMillis = walking.timeStart
                timeStartText.text = "${tempCalendar.get(Calendar.HOUR_OF_DAY)}:${tempCalendar.get(Calendar.MINUTE)}:${tempCalendar.get(Calendar.SECOND)}"
                tempCalendar.timeInMillis = walking.timeEnd
                timeEndText.text = "${tempCalendar.get(Calendar.HOUR_OF_DAY)}:${tempCalendar.get(Calendar.MINUTE)}:${tempCalendar.get(Calendar.SECOND)}"
                totalStepText.text = walking.totalStep.toString()
            } else {
                dateText.text = "Data not found"
                timeStartText.text = "Data not found"
                timeEndText.text = "Data not found"
                totalStepText.text = "Data not found"
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
         * @return A new instance of fragment WalkingDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            WalkingDetailFragment()
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
}