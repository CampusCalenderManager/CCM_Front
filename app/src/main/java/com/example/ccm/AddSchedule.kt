package com.example.ccm

import android.app.AlertDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class AddSchedule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        setDatePicker()
        setAlarmSpinner()
        setGropeSpinner()


        val retrofit = Retrofit.Builder()
            .baseUrl("https://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiAddSchedule = retrofit.create(ApiAddSchedule::class.java)



        apiAddSchedule.postAddSchedule("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTY3OTAzOTIyMSwibWVtYmVySWQiOjF9.IkyEfwU8EiiNB9-zGKFdGOQ2N8F20c-jjXUCQAbWq0kUUS75gxUGGPXwpqA-ml5q9eYejcQ_CaelzTwpx2faqw",
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

    fun setFinalDate(finalDate: TextView) {
        finalDate.setOnClickListener {

            val dialog = AlertDialog.Builder(this).create()
            val edialog: LayoutInflater = LayoutInflater.from(this)
            val mView: View = edialog.inflate(com.example.ccm.R.layout.activity_date_picker, null)

            val year: NumberPicker = mView.findViewById(com.example.ccm.R.id.yearpicker_datepicker)
            val month: NumberPicker =
                mView.findViewById(com.example.ccm.R.id.monthpicker_datepicker)
            val date: NumberPicker = mView.findViewById(com.example.ccm.R.id.datepicker_datepicker)
            val cancel: Button = mView.findViewById(com.example.ccm.R.id.cancel_button_datepicker)
            val save: Button = mView.findViewById(com.example.ccm.R.id.save_button_datepicker)

            val currentTime = Calendar.getInstance().time
            val weekdayFormat = SimpleDateFormat("EE", Locale.KOREA)
            val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
            val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
            val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)

            val currentWeekDay = weekdayFormat.format(currentTime)
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
                finalDate.text =
                    (year.value).toString() + "년 " + (month.value).toString() + "월 " + (date.value).toString() + "일"
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
            val mView: View = edialog.inflate(com.example.ccm.R.layout.activity_date_picker, null)

            val year: NumberPicker = mView.findViewById(com.example.ccm.R.id.yearpicker_datepicker)
            val month: NumberPicker =
                mView.findViewById(com.example.ccm.R.id.monthpicker_datepicker)
            val date: NumberPicker = mView.findViewById(com.example.ccm.R.id.datepicker_datepicker)
            val cancel: Button = mView.findViewById(com.example.ccm.R.id.cancel_button_datepicker)
            val save: Button = mView.findViewById(com.example.ccm.R.id.save_button_datepicker)

            val currentTime = Calendar.getInstance().time
            val weekdayFormat = SimpleDateFormat("EE", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
            val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

            val currentWeekDay = weekdayFormat.format(currentTime)
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
                startDate.text =
                    (year.value).toString() + "년 " + (month.value).toString() + "월 " + (date.value).toString() + "일"
                dialog.dismiss()
                dialog.cancel()
            }

            dialog.setView(mView)
            dialog.create()
            dialog.show()


        }

    }

    fun setDatePicker() {
        val now = System.currentTimeMillis();

        val startDate = findViewById<TextView>(com.example.ccm.R.id.startDate) as TextView
        val finalDate = findViewById<TextView>(com.example.ccm.R.id.finalDate) as TextView

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
        val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
        val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)

        val currentYear = yearFormat.format(currentTime)
        val currentMonth = monthFormat.format(currentTime)
        val currentDate = dateFormat.format(currentTime).toInt()

        finalDate.text = "${currentYear}년 ${currentMonth}월 ${currentDate}일"
        startDate.text = "${currentYear}년 ${currentMonth}월 ${currentDate}일"

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

        val alarmAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alarmArray)

        val alarmSpinner: Spinner = findViewById(R.id.alarmSpinner)
        alarmSpinner.adapter = alarmAdapter
    }

}


//override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_schedule)
//
//        val vDate = findViewById<TextView>(R.id.date_picker_start)
//        vDate.setOnClickListener {
//            val intent = Intent(this, datePickerActivity::class.java)
//            startActivity(intent)
//            Log.e("asdf", "hhh ")
//        }
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item?.itemId) {//코드적기
//
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
