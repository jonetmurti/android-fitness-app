package com.example.workoutapp.scheduler

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.workoutapp.R
import com.example.workoutapp.database.Scheduler
import com.example.workoutapp.database.SchedulerDao
import com.example.workoutapp.database.SchedulerDatabase
import com.example.workoutapp.databinding.FragmentScheduleBinding
import com.example.workoutapp.service.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
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
class SchedulerFragment : Fragment( ) {
    val applicationScope = CoroutineScope(SupervisorJob())

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



    private lateinit var scheduleDao: SchedulerDao
    private var schedules : List<Scheduler>? = null;
    private var newSched : Scheduler? = null;

    // Data
    private var trainingType: Int = 1
    private var exerciseType: String = "Walking"
    private var day: String = "MONDAY"
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

        timeStartPicker.text = convertIntToString(time_start_hour) +':'+ convertIntToString(
            time_start_mins
        )
        timeEndPicker.text = convertIntToString(time_end_hour)+ ':' + convertIntToString(
            time_end_mins
        )

        timeStartPicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tps = activity?.let {
                    TimePickerDialog(
                        it,
                        { view, hourOfday, minute ->

                            timeStartPicker.text =
                                convertIntToString(hourOfday) + ':' + convertIntToString(
                                    minute
                                )
                            time_start_hour = hourOfday
                            time_start_mins = minute
                            if (tps != null) {
                                if (tps!!.isShowing) {
                                    tps?.dismiss()
                                }
                            }

                        }, time_start_hour, time_start_mins, true
                    )
                }

                tps?.show()
            }
        })
        timeEndPicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                tpe = activity?.let {
                    TimePickerDialog(
                        it,
                        { view, hourOfday, minute ->

                            timeEndPicker.text =
                                convertIntToString(hourOfday) + ':' + convertIntToString(
                                    minute
                                )
                            time_end_hour = hourOfday
                            time_end_mins = minute
                            if (tpe != null) {
                                if (tpe!!.isShowing) {
                                    tpe?.dismiss()
                                }
                            }

                        }, time_end_hour, time_end_mins, true
                    )
                }

                tpe?.show()
            }
        })

        val datepicker : TextView = view.findViewById(R.id.datePicker)
        datepicker.text = convertIntToString(c_date)+ "/" + convertIntToString(c_month)+ "/" + c_year.toString()
        datepicker.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dpd = activity?.let {
                    DatePickerDialog(
                        it,
                        { view, year, monthOfYear, dayOfMonth ->


                            datepicker.text =
                                convertIntToString(dayOfMonth) + "/" + convertIntToString(
                                    monthOfYear + 1
                                ) + "/" + year.toString()
                            c_year = year
                            c_month = monthOfYear + 1
                            c_date = dayOfMonth
                            if (dpd != null) {
                                if (dpd!!.isShowing()) {
                                    dpd?.dismiss()
                                }
                            }
                        }, c_year, c_month - 1, c_date
                    )
                }
                dpd?.datePicker!!.setMinDate(System.currentTimeMillis() - 1000)
                dpd?.show()
            }
        })
        val dayPicker : Spinner = view.findViewById(R.id.dayPicker)



        dayPicker?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                day = dayPicker?.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        spTrainingType?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val value: String = spTrainingType?.selectedItem.toString()
                if (value == "One time only") {
                    trainingType = 1
                    datepicker.visibility = View.VISIBLE
                    dayPicker.visibility = View.GONE

                } else if (value == "Daily") {
                    trainingType = 2
                    datepicker.visibility = View.GONE
                    dayPicker.visibility = View.GONE

                } else {
                    trainingType = 3
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
                bt_walking.backgroundTintList = context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.blue_700
                    )
                }
                bt_walking.setTextColor(context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.white
                    )
                })

                bt_cycling.backgroundTintList = context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.gray_200
                    )
                }
                bt_cycling.setTextColor(context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.black
                    )
                })

            }
        })


        bt_cycling.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                exerciseType = "Cycling"

                goalType.text = "km"
                bt_walking.backgroundTintList = context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.gray_200
                    )
                }
                bt_walking.setTextColor(context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.black
                    )
                })
                bt_cycling.backgroundTintList = context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.blue_700
                    )
                }
                bt_cycling.setTextColor(context?.let {
                    ContextCompat.getColorStateList(
                        it,
                        R.color.white
                    )
                })
            }
        })

        val submit : Button = view.findViewById(R.id.bt_submit)

        submit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                scheduleDao =
                    SchedulerDatabase.getDatabase(requireContext().applicationContext).schedulerDao

                val start = Calendar.getInstance()
                val end = Calendar.getInstance()

                val goalText: EditText = view.findViewById(R.id.goal)

                val goal: String = goalText.text.toString()
                start.set(c_year, c_month - 1, c_date, time_start_hour, time_start_mins)
                end.set(c_year, c_month - 1, c_date, time_end_hour, time_end_mins)

                if (end.before(start) || end.equals(start) || goal.trim().isEmpty()) {
                    val text = "Please check all the fields before you submit!"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(context, text, duration)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    var step: Int? = null
                    var km: Int? = null
                    var sched: Scheduler? = null
                    if (exerciseType == "Walking") {
                        step = (goalText.text.toString()).toInt()
                    } else {
                        km = (goalText.text.toString()).toInt()
                    }
                    val startCalendar: Calendar = Calendar.getInstance()
                    val endCalendar: Calendar = Calendar.getInstance()
                    var alarmManager: AlarmManager =
                        context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    val nowCalendar: Calendar = Calendar.getInstance()
                    nowCalendar.timeInMillis = System.currentTimeMillis()

                    if (trainingType == 1) {
                        startCalendar.set(
                            c_year,
                            c_month - 1,
                            c_date,
                            time_start_hour,
                            time_start_mins,
                            0
                        )
                        endCalendar.set(c_year, c_month - 1, c_date, time_end_hour, time_end_mins,0)


                    } else if (trainingType == 2) {
                        startCalendar.timeInMillis = System.currentTimeMillis()
                        startCalendar.set(Calendar.HOUR_OF_DAY, time_start_hour)
                        startCalendar.set(Calendar.MINUTE, time_start_mins)
                        startCalendar.set(Calendar.SECOND,0)

                        endCalendar.timeInMillis = System.currentTimeMillis()
                        endCalendar.set(Calendar.HOUR_OF_DAY, time_end_hour)
                        endCalendar.set(Calendar.MINUTE, time_end_mins)
                        endCalendar.set(Calendar.SECOND,0)
                    } else {
                        startCalendar.timeInMillis = System.currentTimeMillis()
                        startCalendar.set(Calendar.DAY_OF_WEEK, DayOfWeek.valueOf(day).value + 1)
                        startCalendar.set(Calendar.HOUR_OF_DAY, time_start_hour)
                        startCalendar.set(Calendar.MINUTE, time_start_mins)
                        startCalendar.set(Calendar.SECOND,0)

                        Log.d("cal",startCalendar.toString())
                        Log.d("day",DayOfWeek.valueOf(day).value.toString())
                        endCalendar.timeInMillis = System.currentTimeMillis()
                        endCalendar.set(Calendar.DAY_OF_WEEK, DayOfWeek.valueOf(day).value + 1)
                        endCalendar.set(Calendar.HOUR_OF_DAY, time_end_hour)
                        endCalendar.set(Calendar.MINUTE, time_end_mins)
                        endCalendar.set(Calendar.SECOND,0)

                    }
                    sched = Scheduler(
                        0,
                        trainingType,
                        startCalendar.timeInMillis,
                        endCalendar.timeInMillis,
                        exerciseType,
                        km,
                        step
                    )

                    runBlocking {
                        val id: Int = scheduleDao.insert(sched).toInt()
                    }

                    Log.d("alarmstart", startCalendar.toString())
                    Log.d("alarmend", endCalendar.toString())
                    var target : Int = 0
                    if (exerciseType == "Walking"){
                        if (step != null) {
                            target = step
                        }
                    } else {
                        if (km != null) {
                            target = km
                        }
                    }
                    if (trainingType == 1) {
                        setOneTimeAlarm(
                                context!!,
                                startCalendar.timeInMillis,
                                exerciseType,
                                target,
                                true
                            )
                        setOneTimeAlarm(
                            context!!,
                            endCalendar.timeInMillis,
                            exerciseType,
                            target,
                            false
                        )

                    } else if (trainingType == 2) {
                        if (startCalendar.timeInMillis < System.currentTimeMillis()) {
                            setDailyAlarm(
                                context!!,
                                startCalendar.timeInMillis + 1000 * 60 * 60 * 24,
                                exerciseType,
                                target,
                                true
                            )
                            setDailyAlarm(
                                context!!,
                                endCalendar.timeInMillis + 1000 * 60 * 60 * 24,
                                exerciseType,
                                target,
                                false
                            )
                        } else {
                            setDailyAlarm(
                                context!!,
                                startCalendar.timeInMillis,
                                exerciseType,
                                target,
                                true
                            )
                            setDailyAlarm(
                                context!!,
                                endCalendar.timeInMillis,
                                exerciseType,
                                target,
                                false
                            )
                        }
                    } else {
                        if (startCalendar.timeInMillis < System.currentTimeMillis()) {
                            setWeeklyAlarm(
                                context!!,
                                startCalendar.timeInMillis + 1000 * 60 * 60 * 24 * 7,
                                exerciseType,
                                target,
                                true
                            )
                            setWeeklyAlarm(
                                context!!,
                                endCalendar.timeInMillis + 1000 * 60 * 60 * 24 * 7,
                                exerciseType,
                                target,
                                false
                            )
                        } else {
                            setWeeklyAlarm(
                                context!!,
                                startCalendar.timeInMillis,
                                exerciseType,
                                target,
                                true
                            )
                            setWeeklyAlarm(
                                context!!,
                                endCalendar.timeInMillis,
                                exerciseType,
                                target,
                                false
                            )
                        }

                    }

                    val text = "Submitted!"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(context, text, duration)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }


            }
        })
        return view
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

     fun convertIntToString(num: Int): String{
        var str : String = num.toString()
        if (num < 10){
            str = "0" + str
        }
        return str
    }
    fun setOneTimeAlarm(context: Context, time: Long,exerciseType: String,target: Int, start: Boolean) {
        val i = Intent(context, Alarm::class.java)
        if (start == true){
            i.putExtra("start", 1)
        } else {
            i.putExtra("start", 0)
        }
        i.putExtra("exercise", exerciseType)
        i.putExtra("target",target)

        var reqid : Int = System.currentTimeMillis().toInt()
        if(!start){
            reqid *= -1
        }

        val pi = PendingIntent.getBroadcast(context, reqid, i, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time,
            pi
        )
    }

    fun setDailyAlarm(
        context: Context,
        time: Long,exerciseType: String,target: Int, start: Boolean
    ){
        val i = Intent(context, Alarm::class.java)
        if (start == true){
            i.putExtra("start", 1)
        } else {
            i.putExtra("start", 0)
        }
        i.putExtra("exercise", exerciseType)
        i.putExtra("target",target)
        Log.d("alarm", time.toString())
        Log.d("now", (System.currentTimeMillis()+10000).toString())
        var reqid : Int = System.currentTimeMillis().toInt()
        if(!start){
            reqid *= -1
        }
        val pi = PendingIntent.getBroadcast(context, reqid, i, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            1000 * 60 * 60 * 24,
            pi
        )
    }

    fun setWeeklyAlarm(
        context: Context,
        time: Long,exerciseType: String,target: Int, start: Boolean
    ){
        val i = Intent(context, Alarm::class.java)
        if (start == true){
            i.putExtra("start", 1)
        } else {
            i.putExtra("start", 0)
        }
        i.putExtra("exercise", exerciseType)
        i.putExtra("target",target)
        Log.d("alarm", time.toString())
        Log.d("now", (System.currentTimeMillis()+10000).toString())
        var reqid : Int = System.currentTimeMillis().toInt()
        if(!start){
            reqid *= -1
        }
        val pi = PendingIntent.getBroadcast(context, reqid, i, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            1000 * 60 * 60 * 24 * 7,
            pi
        )
    }
    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SchedulerFragment()
    }
}