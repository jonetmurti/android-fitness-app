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
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.R
import com.example.workoutapp.database.TrainingDatabase
import com.example.workoutapp.database.WalkingDao
import com.example.workoutapp.databinding.FragmentLogListBinding
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogListFragment : Fragment(), LogAdapter.LogClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!
    private lateinit var date: LocalDate
    private lateinit var walkingDao: WalkingDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        val view = binding.root
        walkingDao = TrainingDatabase.getDatabase(requireContext().applicationContext).walkingDao
        val args = LogListFragmentArgs.fromBundle(requireArguments())
        date = args.date

        val logDate: TextView = view.findViewById(R.id.logDate)
        logDate.text = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
        val trainingLogs: List<TrainingLog> = loadData()

        binding.logRecycleView.adapter = LogAdapter(this, trainingLogs)
        binding.logRecycleView.setHasFixedSize(true)

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
         * @return A new instance of fragment LogListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                LogListFragment()
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

    private fun loadData(): List<TrainingLog> {
        // TODO: load training data from db
        val walkingByDate = walkingDao.getWalkingByDate(date.toEpochDay())
        Log.d("LOGLIST", walkingByDate.toString())
        return listOf<TrainingLog>(
                TrainingLog(0, "cycling", 0, 1),
                TrainingLog(1, "cycling", 0, 1),
                TrainingLog(2, "walking", 0, 1),
                TrainingLog(3, "walking", 0, 1),
                TrainingLog(4, "cycling", 0, 1)
        )
    }

    override fun onClick(item: TrainingLog) {
        if (item.type == "cycling") {
            findNavController()
                .navigate(LogListFragmentDirections.actionLogListPageToCyclingDetailPage(item.id))
        } else {
            findNavController()
                .navigate(LogListFragmentDirections.actionLogListPageToWalkingDetailPage(item.id))
        }
    }
}