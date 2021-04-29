package com.example.workoutapp.history

import android.os.Bundle
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
class LogListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!
    private lateinit var date: LocalDate

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
        val args = LogListFragmentArgs.fromBundle(requireArguments())
        date = args.date

        val logDate: TextView = view.findViewById(R.id.logDate)
        logDate.text = "${date.dayOfMonth}/${date.monthValue}/${date.year}"
        val trainingLogs: List<TrainingLog> = loadData()
        // TODO: create recycle view
        val recycleView: RecyclerView = view.findViewById(R.id.logRecycleView)
        recycleView.adapter = LogAdapter(trainingLogs)
        recycleView.setHasFixedSize(true)

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
        return listOf<TrainingLog>(
                TrainingLog("zero"),
                TrainingLog("one"),
                TrainingLog("two"),
                TrainingLog("three"),
                TrainingLog("four")
        )
    }
}