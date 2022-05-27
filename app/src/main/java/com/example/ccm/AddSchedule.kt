package com.example.ccm

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import com.example.ccm.API.*
import com.example.ccm.CCMApp.Companion.userLocalDB
import com.example.ccm.LocalDB.User
import com.example.ccm.databinding.ActivityAddScheduleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

var postStartDate = ""
var postEndDate = ""
var postStartAlarm = ""
var categoryList = mutableListOf<Category>()

class AddSchedule : AppCompatActivity() {
    lateinit var binding: ActivityAddScheduleBinding
    lateinit var retrofit: Retrofit

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryList.clear() // 업데이트된 카테고리 리스트는 항상 초기화 해주어야한다.

        // api 요청을 위한 retrofit 객체 생성
        retrofit = Retrofit.Builder()
            .baseUrl("http://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 초기 세팅
        setDatePicker()
        setAlarmSpinner()
        setGropeSpinner()

        // 취소 버튼 클릭시 발생할 이벤트
        binding.addScheduleCancelButton.setOnClickListener {
            val intent = Intent(binding.root.context, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // DB 에 저장될 스케줄 리스트
        val updatedSchedule = mutableListOf<CalendarScheduleObject>()

        // 일정 추가 버튼을 누르면 발생하는 이벤트
        binding.addScheduleSaveButton.setOnClickListener {
            // 입력된 값들 정리
            setPostStartAlarm()
            val postTitle = binding.addScheduleTitle.text.toString()
            val postContent = binding.addScheduleContent.text.toString()
            val postIsShared = binding.addScheduleIsShared.showText.toString().toBoolean()
            val postIsAlarm = binding.addScheduleIsAlarm.showText.toString().toBoolean()
            var postOrganizationID = ""
            var categoryColor = ""

            // 선택된 그룹이 가진 id와 color 값 정리
            categoryList.forEach { category ->
                if (category.name == binding.groupSpinner.selectedItem.toString()) {
                    postOrganizationID = category.categoryId!!
                    categoryColor = category.color!!
                }
            }

            // api 요청을 위한 인터페이스 객체 생성
            val apiAddSchedule = retrofit.create(ApiAddSchedule::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val users = CoroutineScope(Dispatchers.IO).async {
                    userLocalDB.userDao().getAll()
                }.await()

                // 로컬 스케줄을 업데이트 하기 위한 스케줄 리스트에 넣기
                users[0].userSchedule!!.forEach { schedule ->
                    updatedSchedule.add(schedule)
                }

                // 추가될 스케줄을 업데이트 하기 위한 스케줄 리스트에 넣기
                updatedSchedule.add(
                    CalendarScheduleObject(
                        id = postOrganizationID.toInt(),
                        color = Color.parseColor(categoryColor),
                        text = postTitle,
                        startDate = LocalDateTime.parse(postStartDate + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        endDate = LocalDateTime.parse(postEndDate + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        isHoliday = false
                    )
                )

                // 로컬 스케줄 업데이트
                users[0].userSchedule = updatedSchedule

                // 로컬 카테고리 업데이트
                Log.e("beforeUpdate category", users[0].userCategory.toString())
                Log.e("afterUpdate category", categoryList.toString())
                users[0].userCategory = categoryList
                Log.e("update category", users[0].userCategory.toString())

                // 업데이트 내역 로컬 DB 에 저장
                CoroutineScope(Dispatchers.IO).async {
                    userLocalDB.userDao().update(users[0])
                }

                // 개인 일정이 아니라면 서버에 데이터 POST
                if (postOrganizationID.toInt() > -1) {
                    apiAddSchedule.postAddSchedule(users[0].userToken!!,
                        AddScheduleJSON(
                            title = postTitle,
                            content = postContent,
                            startDate = postStartDate,
                            endDate = postEndDate,
                            startAlarm = postStartAlarm,
                            isShared = postIsShared,
                            isAlarm = postIsAlarm,
                            organizationId = postOrganizationID.toLong()
                        )
                    ).enqueue(object : Callback<GetScheduleJSON> {
                        override fun onResponse(
                            call: Call<GetScheduleJSON>,
                            response: Response<GetScheduleJSON>,
                        ) {
                            // 일정 추가가 성공했다면 메인으로 가기
                            Toast.makeText(
                                binding.root.context,
                                "일정이 성공적으로 등록되었어요",
                                Toast.LENGTH_LONG
                            ).show()

                            val intent = Intent(binding.root.context, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<GetScheduleJSON>, t: Throwable) {
                            Log.e(TAG, "실패 : $t")
                        }
                    })
                } else {
                    // 서버에 저장하는게 아니면 바로 메인으로 가기
                    Toast.makeText(
                        binding.root.context,
                        "일정이 성공적으로 등록되었어요",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    // 그룹 목록 세팅
    fun setGropeSpinner() {
        val groupList = mutableListOf<String>() // 스피너에 들어갈 그룹 리스트

        val groupAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            groupList
        )

        val groupSpinner = binding.groupSpinner
        groupSpinner.adapter = groupAdapter

        // 로컬 DB 에서 그룹 리스트 받아와서 넣기
        CoroutineScope(Dispatchers.Main).launch {
            val users = CoroutineScope(Dispatchers.IO).async {
                userLocalDB.userDao().getAll()
            }.await()

            // 로컬의 카테고리 넣어주기

            Log.e("before init categoryList", users[0].userCategory.toString())

            users[0].userCategory!!.forEach { category ->
                if (category.name == "개인") {
                    groupList.add(category.name)
                    categoryList.add(category)
                }
            }

            Log.e("init CategoryList", categoryList.toString())

            CoroutineScope(Dispatchers.IO).async {
                val apiGetMyOrganization = retrofit.create(APIGetMyOrganization::class.java)
                apiGetMyOrganization.getMyOrganization(users[0].userToken!!)
                    .enqueue(object : Callback<MyOrganizationJSON> {
                        override fun onResponse(
                            call: Call<MyOrganizationJSON>,
                            response: Response<MyOrganizationJSON>,
                        ) {
                            val serverGroupList = response.body()!!.organizationInfoResponseList
                            Log.e("serverGroupList", serverGroupList.toString())
                            serverGroupList.forEach { organizationInfo ->
                                groupList.add(organizationInfo.title)
                                categoryList.add(
                                    Category(
                                        false,
                                        categoryId = organizationInfo.organizationId.toString(),
                                        color = organizationInfo.color,
                                        name = organizationInfo.title
                                    )
                                )
                                groupAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onFailure(call: Call<MyOrganizationJSON>, t: Throwable) {
                            Log.d(TAG, "실패 : $t")
                            Toast.makeText(
                                binding.root.context,
                                "요청이 잘못되었어요",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            }.await()
        }
    }

    // 날짜 피커 설정
    fun setDatePicker() {
        val startDate = binding.startDate
        val finalDate = binding.finalDate

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
        val currentMinute = minuteFormat.format(now).toInt()

        finalDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"
        startDate.text =
            "${currentYear}년 ${currentMonth}월 ${currentDate}일 ${currentHour}시 ${currentMinute}분"

        postStartDate = "${currentYear}-${
            String.format("%02d", currentMonth.toInt())
        }-${currentDate}T${
            String.format("%02d",currentHour)
        }:${
            String.format("%02d",currentMinute)
        }:00"

        postEndDate = postStartDate

        setStartDate(startDate)
        setFinalDate(finalDate)
    }

    // 종료 날짜 설정
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

    // 시작 날짜 설정
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

    // 알람 목록 세팅
    fun setAlarmSpinner() {
        val alarmArray = arrayOf("15분 전", "30분 전", "1시간 전", "3시간 전", "6시간 전", "12시간 전", "하루 전")
        val alarmSpinner: Spinner = findViewById(R.id.alarmSpinner)

        val alarmAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alarmArray)
        alarmSpinner.adapter = alarmAdapter
    }

    // 시작 시간 피커 설정
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
                    String.format("%02d",minute.value)
                }분")
            dialog.dismiss()
            dialog.cancel()
        }

        postStartDate =
            "${year}-${
                String.format("%02d", month)
            }-${date}T${
                String.format("%02d",hour.value)
            }:${
                String.format("%02d",minute.value)
            }:00"

        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }

    // 마지막 시간 피커 설정
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

        postEndDate = "${year}-${
            String.format("%02d", month)
        }-${date}T${
            String.format("%02d",hour.value)
        }:${
            String.format("%02d",minute.value)
        }:00"

        dialog.setView(mView)
        dialog.create()
        dialog.show()

    }

    // 알람 설정
    fun setPostStartAlarm() {
        val alarmSpinner: Spinner = findViewById(R.id.alarmSpinner)

        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss")
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

    override fun onBackPressed() {
        val intent = Intent(binding.root.context, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


