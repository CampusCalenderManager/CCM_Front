package com.example.ccm

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ccm.API.ApiAddSchedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*


var postTitle = ""
var postStartDate = ""
var postEndDate = ""
var postStartAlarm = ""
var postIsShared = ""
var postColor = ""
var postOrganizationId = ""

class AddSchedule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        setDatePicker()
        setAlarmSpinner()
        setGropeSpinner()



        val saveButton = findViewById<Button>(R.id.add_schedule_save_button)
        Log.e("clicked", "clicked")

        saveButton.setOnClickListener {
        //ccmApp.prefs.token.toString()

            setStartAlarm()

//            postStartAlarm =

            val retrofit = Retrofit.Builder()
                .baseUrl("https://jenkins.argos.or.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiAddSchedule = retrofit.create(ApiAddSchedule::class.java)

            apiAddSchedule.postAddSchedule("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTY3OTM5MDg5MiwibWVtYmVySWQiOjV9.FwNVVxk2JPC3EUNRlI8K6lEyBWF7IWcaxEvr1Yki_5QytUgzwKaOlnYy6g3n-Ot72Cuagv79qVAr56Ht5ErFzQ",
                AddScheduleJson(
                    "o",
                    "2022-05-14T00:00:00",
                    "2022-05-14T00:00:00",
                    "2022-05-14T00:00:00",
                    "2022-05-14T00:00:00",
                    "false",
                    "red",
                    "1"
                )
            ).enqueue(object : Callback<AddScheduleJson> {
                override fun onResponse(
                    call: Call<AddScheduleJson>,
                    response: Response<AddScheduleJson>,
                ) {
                    Log.d(TAG, "성공 : ${response.raw()} ${response.message()}")
                }

                override fun onFailure(call: Call<AddScheduleJson>, t: Throwable) {
                    Log.d(TAG, "실패 : $t")
                }
            })
        }


    }

    fun setFinalDate(finalDate: TextView) {
        finalDate.setOnClickListener {

            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.activity_date_picker, null)

            val year: NumberPicker = mView.findViewById(R.id.yearpicker_datepicker)
            val month: NumberPicker =
                mView.findViewById(R.id.monthpicker_datepicker)
            val date: NumberPicker = mView.findViewById(R.id.datepicker_datepicker)
            val cancel: Button = mView.findViewById(R.id.cancel_button_datepicker)
            val save: Button = mView.findViewById(R.id.save_button_datepicker)

            val currentTime = Calendar.getInstance().time
            Log.e(TAG, currentTime.toString())
            val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
            val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
            val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)

            val currentYear = yearFormat.format(currentTime)
            val currentMonth = monthFormat.format(currentTime)
            val currentDate = dateFormat.format(currentTime)


            //  순환 안되게 막기
            year.wrapSelectorWheel = false
            month.wrapSelectorWheel = false
            date.wrapSelectorWheel = false

            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            date.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


            //  최소값 설정
            year.minValue = 2021
            month.minValue = 1
            date.minValue = 1

            //  최대값 설정
            year.maxValue = 2030
            month.maxValue = 12
            date.maxValue = 31

            //기본값 설정인데 왜 안되지??
            year.value = currentYear.toInt()
            month.value = currentMonth.toInt()
            date.value = currentDate.toInt()


            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {
                setFinalTimePicker(finalDate, year.value, month.value, date.value)
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()


        }
    }


    fun setStartDate(startDate: TextView) {
        startDate.setOnClickListener {

            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(R.layout.activity_date_picker, null)

            val year: NumberPicker = mView.findViewById(R.id.yearpicker_datepicker)
            val month: NumberPicker =
                mView.findViewById(R.id.monthpicker_datepicker)
            val date: NumberPicker = mView.findViewById(R.id.datepicker_datepicker)
            val cancel: Button = mView.findViewById(R.id.cancel_button_datepicker)
            val save: Button = mView.findViewById(R.id.save_button_datepicker)

            val currentTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

            val currentYear = yearFormat.format(currentTime)
            val currentMonth = monthFormat.format(currentTime)
            val currentDate = dateFormat.format(currentTime)


            //  순환 안되게 막기
            year.wrapSelectorWheel = false
            month.wrapSelectorWheel = false
            date.wrapSelectorWheel = false

            //  editText 설정 해제
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            date.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


            //  최소값 설정
            year.minValue = 2022
            month.minValue = 1
            date.minValue = 1

            //  최대값 설정
            year.maxValue = 2030
            month.maxValue = 12
            date.maxValue = 31

            year.value = currentYear.toInt()
            month.value = currentMonth.toInt()
            date.value = currentDate.toInt()


            //  취소 버튼 클릭 시
            cancel.setOnClickListener {
                dialog.dismiss()
                dialog.cancel()
            }

            //  완료 버튼 클릭 시
            save.setOnClickListener {
                setStartTimePicker(startDate, year.value, month.value, date.value)
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()


        }

    }

    fun setDatePicker() {
        val startDate = findViewById<TextView>(com.example.ccm.R.id.startDate) as TextView
        val finalDate = findViewById<TextView>(com.example.ccm.R.id.finalDate) as TextView

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
        val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
        val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)
        val hourFormat = SimpleDateFormat("hh", Locale.KOREA)
        val minuteFormat = SimpleDateFormat("mm", Locale.KOREA)

        val currentYear = yearFormat.format(currentTime)
        val currentMonth = monthFormat.format(currentTime)
        val currentDate = dateFormat.format(currentTime).toInt()
        val currentHour = hourFormat.format(currentTime).toInt() + 9
        val currentMinute = minuteFormat.format(currentTime)

        finalDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"
        startDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"

        setStartDate(startDate)
        setFinalDate(finalDate)

    }

    fun setGropeSpinner() {
        val groupArray = arrayOf("자료구조(02분반)",
            "상남자 스터디",
            "기초프로젝트랩(01분반)",
            "컴퓨터프로그래밍3(03분반)",
            "확률및통계",
            "논리회로",
            "계산이론")

        val groupeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groupArray)

        val groupSpinner: Spinner = findViewById(R.id.groupSpinner)
        groupSpinner.adapter = groupeAdapter
    }


    fun setAlarmSpinner() {
        val alarmArray = arrayOf("15분 전", "30분 전", "1시간 전", "3시간 전", "6시간 전", "12시간 전", "하루 전")
        val alarmSpinner:Spinner = findViewById(R.id.alarmSpinner)

        val alarmAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alarmArray)


        alarmSpinner.adapter = alarmAdapter


    }


    private fun setStartTimePicker(startDate: TextView, year: Int, month: Int, date: Int) {
        val currentTime = Calendar.getInstance()
        val dialog = AlertDialog.Builder(this).create()
        val edialog: LayoutInflater = LayoutInflater.from(this)
        val mView: View = edialog.inflate(R.layout.time_picker, null)


        val hour: NumberPicker =
            mView.findViewById(com.example.ccm.R.id.hour_timepicker)
        val minute: NumberPicker = mView.findViewById(com.example.ccm.R.id.minute_timepicker)
        val cancel: Button = mView.findViewById(com.example.ccm.R.id.cancel_button_timePicker)
        val save: Button = mView.findViewById(com.example.ccm.R.id.save_button_timePicker)


        val formatter = NumberPicker.Formatter { value ->
            val temp = value * 5
            "" + temp
        }
        minute.setFormatter(formatter)

        //  순환 안되게 막기
        hour.wrapSelectorWheel = false
        minute.wrapSelectorWheel = false

        //  editText 설정 해제
        hour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        minute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        //  최소값 설정
        hour.minValue = 0
        minute.minValue = 0

        //  최대값 설정
        hour.maxValue = 24
        minute.maxValue = 11

        val now: Long = System.currentTimeMillis()

        val mDate = Date(now)
        val simpleHour = SimpleDateFormat("hh")
        val simpleMinute = SimpleDateFormat("mm")
        val getHour = simpleHour.format(mDate)
        val getMinute = simpleMinute.format(mDate)

        //기본값 설정인데 왜 안되지??
        hour.value = getHour.toInt() + 9
        minute.value = 6


        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        //  완료 버튼 클릭 시
        save.setOnClickListener {
            startDate.text =
                ("${year}년 ${String.format("%02d",month)}월 ${String.format("%02d",date)}일 ${String.format("%02d",hour.value)}시 ${String.format("%02d",minute.value * 5)}분")
            dialog.dismiss()
            dialog.cancel()
        }

        postStartDate = "${year}-${String.format("%02d",month)}-${date}T${hour.value}:${minute.value * 5}:00"

        dialog.setView(mView)
        dialog.create()
        dialog.show()


    }


    private fun setFinalTimePicker(finalDate: TextView, year: Int, month: Int, date: Int) {
        val currentTime = Calendar.getInstance()
        val dialog = AlertDialog.Builder(this).create()
        val edialog: LayoutInflater = LayoutInflater.from(this)
        val mView: View = edialog.inflate(R.layout.time_picker, null)


        val hour: NumberPicker =
            mView.findViewById(R.id.hour_timepicker)
        val minute: NumberPicker = mView.findViewById(R.id.minute_timepicker)
        val cancel: Button = mView.findViewById(R.id.cancel_button_timePicker)
        val save: Button = mView.findViewById(R.id.save_button_timePicker)




        val formatter = NumberPicker.Formatter { value ->
            val temp = value * 5
            "" + temp
        }
        minute.setFormatter(formatter)

        //  순환 안되게 막기
        hour.wrapSelectorWheel = false
        minute.wrapSelectorWheel = false

        //  editText 설정 해제
        hour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        minute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        //  최소값 설정
        hour.minValue = 0
        minute.minValue = 0

        //  최대값 설정
        hour.maxValue = 24
        minute.maxValue = 11

        val now: Long = System.currentTimeMillis()

        val mDate = Date(now)
        val simpleHour = SimpleDateFormat("hh")
        val simpleMinute = SimpleDateFormat("mm")
        val getHour = simpleHour.format(mDate)
        val getMinute = simpleMinute.format(mDate)

        //기본값 설정인데 왜 안되지??
        hour.value = getHour.toInt() + 9
        minute.value = 6


        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        //  완료 버튼 클릭 시
        save.setOnClickListener {

            finalDate.text =
                ("${year}년 ${String.format("%02d",month)}월 ${String.format("%02d",date)}일 ${String.format("%02d",hour.value)}시 ${String.format("%02d",minute.value * 5)}분")
            dialog.dismiss()
            dialog.cancel()
        }

        postEndDate = "${year}-${String.format("%02d",month)}-${date}T${hour.value}:${minute.value * 5}:00"


        dialog.setView(mView)
        dialog.create()
        dialog.show()


    }
    fun setStartAlarm(){
        val alarmSpinner:Spinner = findViewById(R.id.alarmSpinner)

        val formatter : SimpleDateFormat = SimpleDateFormat("yyyy-MM-d'T'HH:mm")
        val date : Date = formatter.parse(postStartDate)

//        (today.time.time - date.time) / (60 * 60 * 24 * 1000)
        Log.e(TAG, date.time.toString())
        postStartAlarm =postStartDate
        when(alarmSpinner.selectedItemId.toInt()){
            0 -> {
                postStartAlarm = (date.time - 15*60 * 1000).toString()
                val date2  = formatter.format(postStartAlarm.toLong())
                Log.e("zz",date2)
            }
        }

    }


}


