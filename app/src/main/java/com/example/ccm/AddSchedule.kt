package com.example.ccm

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ccm.API.AddScheduleJSON
import com.example.ccm.API.ApiAddSchedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


var postTitle = ""
var postContent = ""
var postStartDate = ""
var postEndDate = ""
var postStartAlarm = ""
var postIsShared = ""
var postIsAlarm = ""
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


            setPostStartAlarm()
            setPostTitle()
            setPostContent()
            setPostIsShared()
            setPostIsAlarm()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://jenkins.argos.or.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiAddSchedule = retrofit.create(ApiAddSchedule::class.java)

            //accesstoken에 ccmApp.prefs.token.toString()
            apiAddSchedule.postAddSchedule("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTY3OTM5MDg5MiwibWVtYmVySWQiOjV9.FwNVVxk2JPC3EUNRlI8K6lEyBWF7IWcaxEvr1Yki_5QytUgzwKaOlnYy6g3n-Ot72Cuagv79qVAr56Ht5ErFzQ",
                AddScheduleJSON(
                    postTitle,
                    postContent,
                    postStartDate,
                    postEndDate,
                    postStartAlarm,
                    postIsShared,
                    postIsAlarm,
                    "#ffffff",
                    "123"

                )
            ).enqueue(object : Callback<AddScheduleJSON> {
                override fun onResponse(
                    call: Call<AddScheduleJSON>,
                    response: Response<AddScheduleJSON>,
                ) {
                    Log.d(TAG, "성공 : ${response.raw()} ${response.message()}")
                }

                override fun onFailure(call: Call<AddScheduleJSON>, t: Throwable) {
                    Log.d(TAG, "실패 : $t")
                }
            })
        }


    }

    private fun setPostIsAlarm() {
        val isAlarm = findViewById<Switch>(R.id.add_schedule_isAlarm)
        postIsAlarm = isAlarm.showText.toString()
    }

    private fun setPostIsShared() {
        val isShared = findViewById<Switch>(R.id.add_schedule_isShared)
        postIsShared = isShared.showText.toString()
    }

    private fun setPostContent() {
        val content = findViewById<EditText>(R.id.add_schedule_content)
        postContent = content.text.toString()
    }

    private fun setPostTitle() {
        val title = findViewById<EditText>(R.id.add_schedule_title)
        postTitle = title.text.toString()
    }

    private fun setFinalDate(finalDate: TextView) {
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

            val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
            val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
            val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)


            val now: Long = System.currentTimeMillis() + 9 * 60 * 60 * 1000


            val currentYear = yearFormat.format(now)
            val currentMonth = monthFormat.format(now)
            val currentDate = dateFormat.format(now)


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

            val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
            val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
            val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)


            val now: Long = System.currentTimeMillis() + 9 * 60 * 60 * 1000


            val currentYear = yearFormat.format(now)
            val currentMonth = monthFormat.format(now)
            val currentDate = dateFormat.format(now)


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

        val dateFormat = SimpleDateFormat("dd", Locale.KOREA)
        val monthFormat = SimpleDateFormat("MM", Locale.KOREA)
        val yearFormat = SimpleDateFormat("yyyy", Locale.KOREA)
        val hourFormat = SimpleDateFormat("HH", Locale.KOREA)
        val minuteFormat = SimpleDateFormat("mm", Locale.KOREA)

        val now: Long = System.currentTimeMillis() + 9 * 60 * 60 * 1000

        val currentYear = yearFormat.format(now)
        val currentMonth = monthFormat.format(now)
        val currentDate = dateFormat.format(now).toInt()
        val currentHour = hourFormat.format(now).toInt()
        val currentMinute = minuteFormat.format(now)



        finalDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"
        startDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"

        postStartDate = "${currentYear}-${
            String.format("%02d",
                currentMonth.toInt())
        }-${currentDate}T${
            String.format("%02d",
                currentHour)
        }:${(currentMinute.toInt())}:00"
        setStartDate(startDate)
        setFinalDate(finalDate)

    }

    // Todo : 그룹 받아와서 array에 넣고 organizationid과 같이 api요청
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
        val alarmSpinner: Spinner = findViewById(R.id.alarmSpinner)

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
            mView.findViewById(R.id.hour_timepicker)
        val minute: NumberPicker = mView.findViewById(R.id.minute_timepicker)
        val cancel: Button = mView.findViewById(R.id.cancel_button_timePicker)
        val save: Button = mView.findViewById(R.id.save_button_timePicker)


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
        hour.maxValue = 23
        minute.maxValue = 59

        val now: Long = System.currentTimeMillis() + 9 * 60 * 60 * 1000


        val mDate = Date(now)

        val simpleHour = SimpleDateFormat("HH")
        val simpleMinute = SimpleDateFormat("mm")
        val getHour = simpleHour.format(mDate)
        val getMinute = simpleMinute.format(mDate)

        //기본값 설정인데 왜 안되지??
        hour.value = getHour.toInt()
        minute.value = 30


        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        //  완료 버튼 클릭 시
        save.setOnClickListener {
            startDate.text =
                ("${year}년 ${String.format("%02d", month)}월 ${
                    String.format("%02d",
                        date)
                }일 ${String.format("%02d", hour.value)}시 ${
                    String.format("%02d",
                        minute.value)
                }분")
            dialog.dismiss()
            dialog.cancel()
        }

        postStartDate =
            "${year}-${String.format("%02d", month)}-${date}T${hour.value}:${minute.value}:00"

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
        hour.maxValue = 23
        minute.maxValue = 59

        val now: Long = System.currentTimeMillis() + 9 * 60 * 60 * 1000

        val mDate = Date(now)
        val simpleHour = SimpleDateFormat("HH")
        val simpleMinute = SimpleDateFormat("mm")
        val getHour = simpleHour.format(mDate)
        val getMinute = simpleMinute.format(mDate)

        //기본값 설정인데 왜 안되지??
        hour.value = getHour.toInt()
        minute.value = 30


        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        //  완료 버튼 클릭 시
        save.setOnClickListener {
            finalDate.text =
                ("${year}년 ${String.format("%02d", month)}월 ${
                    String.format("%02d",
                        date)
                }일 ${String.format("%02d", hour.value)}시 ${
                    String.format("%02d",
                        minute.value)
                }분")
            dialog.dismiss()
            dialog.cancel()
        }

        postEndDate =
            "${year}-${String.format("%02d", month)}-${date}T${hour.value}:${minute.value}:00"


        dialog.setView(mView)
        dialog.create()
        dialog.show()


    }

    fun setPostStartAlarm() {
        val alarmSpinner: Spinner = findViewById(R.id.alarmSpinner)

        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-d'T'HH:mm")
        val date: Date = formatter.parse(postStartDate)

        Log.e(TAG, date.time.toString())
        when (alarmSpinner.selectedItemId.toInt()) {
            0 -> {
                postStartAlarm = formatter.format(date.time - 15 * 60 * 1000)//15분전
            }
            1 -> {
                postStartAlarm = formatter.format(date.time - 30 * 60 * 1000)
            }
            2 -> {
                postStartAlarm = formatter.format(date.time - 60 * 60 * 1000)
            }
            3 -> {
                postStartAlarm = formatter.format(date.time - 3 * 60 * 60 * 1000)
            }
            4 -> {
                postStartAlarm = formatter.format(date.time - 6 * 60 * 60 * 1000)
            }
            5 -> {
                postStartAlarm = formatter.format(date.time - 12 * 60 * 60 * 1000)
            }
            6 -> {
                postStartAlarm = formatter.format(date.time - 24 * 60 * 60 * 1000)
            }
        }

    }


}


