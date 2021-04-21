package com.example.workoutapp.scheduler

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.workoutapp.R
import com.example.workoutapp.databinding.FragmentScheduleBinding
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SchedulerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SchedulerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    // Object in UI
    private var spTrainingType: Spinner? = null
    private var dpd: DatePickerDialog? = null
    private var tps: TimePickerDialog? = null
    private var tpe: TimePickerDialog? = null

    // Data
    private var exerciseType: String = "Walking"
    private var time_start_hour: Int = Calendar.getInstance().get(Calendar.HOUR)
    private var time_start_mins: Int = Calendar.getInstance().get(Calendar.MINUTE)
    private var time_end_hour: Int = Calendar.getInstance().get(Calendar.HOUR)
    private var time_end_mins: Int = Calendar.getInstance().get(Calendar.MINUTE)
    private var c_year: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var c_month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var c_date: Int = Calendar.getInstance().get(Calendar.DATE)


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
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val view = binding.root
        spTrainingType  = view.findViewById(R.id.training_type)
        val timeStartPicker : TextView = view.findViewById(R.id.timeStartPicker)
        val timeEndPicker : TextView = view.findViewById(R.id.timeEndPicker)

        timeStartPicker.text = convertIntToString(time_start_hour) +':'+ convertIntToString(time_start_mins)
        timeEndPicker.text = convertIntToString(time_end_hour)+ ':' + convertIntToString(time_end_mins)

        timeStartPicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tps = activity?.let {
                    TimePickerDialog(it,
                             { view, hourOfday, minute ->

                                timeStartPicker.text = convertIntToString(hourOfday) + ':' + convertIntToString(minute)
                                time_start_hour = hourOfday
                                time_start_mins = minute
                                if (tps != null) {
                                    if (tps!!.isShowing) {
                                        tps?.dismiss()
                                    }
                                }

                            }, time_start_hour, time_start_mins, true)
                }

                tps?.show()
            }
        })
        timeEndPicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tpe = activity?.let {
                    TimePickerDialog(it,
                             { view, hourOfday, minute ->

                                timeEndPicker.text = convertIntToString(hourOfday) + ':' + convertIntToString(minute)
                                time_end_hour = hourOfday
                                time_end_mins = minute
                                if (tpe != null) {
                                    if (tpe!!.isShowing) {
                                        tpe?.dismiss()
                                    }
                                }

                            }, time_end_hour, time_end_mins, true)
                }

                tpe?.show()
            }
        })

        val datepicker : TextView = view.findViewById(R.id.datePicker)
        datepicker.text = convertIntToString(c_date)+ "/" + convertIntToString(c_month)+ "/" + c_year.toString()
        datepicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dpd = activity?.let {
                    DatePickerDialog(it,
                            { view, year, monthOfYear, dayOfMonth ->


                                datepicker.text = convertIntToString(dayOfMonth) + "/" + convertIntToString(monthOfYear + 1) + "/" + year.toString()
                                c_year = year
                                c_month = monthOfYear + 1
                                c_date = dayOfMonth
                                if (dpd != null) {
                                    if (dpd!!.isShowing()) {
                                        dpd?.dismiss()
                                    }
                                }
                            }, c_year, c_month - 1, c_date)
                }
                dpd?.datePicker!!.setMinDate(System.currentTimeMillis() - 1000)
                dpd?.show()
            }
        })
        val dayPicker : Spinner = view.findViewById(R.id.dayPicker)
        spTrainingType?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View,
                    position: Int,
                    id: Long
            ) {
                val value: String = spTrainingType?.selectedItem.toString()
                if (value == "One time only") {
                    datepicker.visibility = View.VISIBLE
                    dayPicker.visibility = View.GONE

                } else if (value == "Daily") {
                    datepicker.visibility = View.GONE
                    dayPicker.visibility = View.GONE

                } else {
                    datepicker.visibility = View.GONE
                    dayPicker.visibility = View.VISIBLE
                }
                // your code here
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        val bt_cycling : Button = view.findViewById(R.id.bt_cycling)
        val bt_walking : Button = view.findViewById(R.id.bt_walking)
        val goalType : TextView = view.findViewById(R.id.goalType)

        bt_walking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                exerciseType = "Walking"

                goalType.text = "steps"
                bt_walking.backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.blue_700) }
                bt_walking.setTextColor(context?.let { ContextCompat.getColorStateList(it, R.color.white) })

                bt_cycling.backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.gray_200) }
                bt_cycling.setTextColor(context?.let { ContextCompat.getColorStateList(it, R.color.black) })

            }
        })


        bt_cycling.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                exerciseType = "Cycling"

                goalType.text = "km"
                bt_walking.backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.gray_200) }
                bt_walking.setTextColor(context?.let { ContextCompat.getColorStateList(it, R.color.black) })
                bt_cycling.backgroundTintList = context?.let { ContextCompat.getColorStateList(it, R.color.blue_700) }
                bt_cycling.setTextColor(context?.let { ContextCompat.getColorStateList(it, R.color.white) })
            }
        })

        val submit : Button = view.findViewById(R.id.bt_submit)

        submit.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                val goalText : EditText = view.findViewById(R.id.goal)

                val goal : String = goalText.text.toString()
                start.set(c_year, c_month-1, c_date, time_start_hour, time_start_mins)
                end.set(c_year, c_month-1, c_date, time_end_hour, time_end_mins)

                if (end.before(start) || end.equals(start) || goal.trim().isEmpty()) {
                    val text = "Please check all the fields before you submit!"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(context, text, duration)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        })
        return view
    }

     fun convertIntToString(num: Int): String{
        var str : String = num.toString()
        if (num < 10){
            str = "0" + str
        }
        return str
    }
    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SchedulerFragment()
    }
}