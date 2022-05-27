package com.example.ccm

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.example.ccm.API.*
import com.example.ccm.CCMApp.Companion.userLocalDB
import com.example.ccm.LocalDB.User
import com.example.ccm.databinding.ActivityMainBinding
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var retrofit: Retrofit

    val categoryItems = mutableListOf<Category>()

    val scheduleItems = mutableListOf<CalendarScheduleObject>()

    var isUserLogin = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // api 호출을 위한 retrofit 인스턴스 생성
        retrofit = Retrofit.Builder()
            .baseUrl("http://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 로컬 DB 는 비동기로 호출해야한다. 따라서 코루틴을 사용한다.
        CoroutineScope(Dispatchers.Main).launch {
            // 먼저 유저 목록을 불러온다.
            // 사용자는 하나이므로 리스트의 첫번째를 사용하면 된다.
            val users = CoroutineScope(Dispatchers.IO).async {
                userLocalDB.userDao().getAll()
            }.await()

            // 앱을 처음 사용한다면 user 라는 이름으로 로컬 db에 생성
            if (users.isEmpty()) {
                val categoryListBase = listOf(
                    Category(true, "-2", null, "통합", "user"),
                    Category(false, "-1", "#59bfff", "개인", "user"),
                )

                CoroutineScope(Dispatchers.IO).launch {
                    userLocalDB.userDao().insert(
                        User(
                            "user",
                            null,
                            categoryListBase,
                            listOf()
                        )
                    )
                }

                categoryListBase.forEach { category ->
                    categoryItems.add(category)
                }

                binding.calendarHeaderUserIcon.text = "user"

                binding.calendarCategoryRv.adapter!!.notifyDataSetChanged()
            } else {
                // 앱을 실행이 처음이 아니라면 우선 로컬 db 에서 데이터를 가져온다.
                categoryItems.add(Category(true, "-2", null, "통합", users[0].username))
                users[0].userCategory?.forEach { category ->
                    if (category.name != "통합") {
                        if (category.name == "개인") {
                            categoryItems.add(
                                Category(
                                    isSelected = category.isSelected,
                                    categoryId = category.categoryId,
                                    color = category.color,
                                    name = category.name,
                                    owner = users[0].username
                                )
                            )
                        } else {
                            categoryItems.add(category)
                        }
                    }
                }

                binding.calendarCategoryRv.adapter!!.notifyDataSetChanged() // 오프라인일 경우를 대비

                users[0].userSchedule?.forEach { schedule ->
                    scheduleItems.add(schedule)
                }
                binding.calendarView.setSchedules(scheduleItems)

                binding.calendarHeaderUserIcon.text = users[0].username

                // 토큰의 존재 여부를 통해 로그인 되었는지를 판단한다.
                isUserLogin = users[0].userToken != null
            }

            if (isUserLogin) {
                updateUserCategory(users[0].userToken!!)

                // 서버로부터 업데이트 된 내용들을 로컬이랑 맞춘다.
                users[0].userCategory = categoryItems
                users[0].userSchedule = scheduleItems

                CoroutineScope(Dispatchers.IO).launch {
                    userLocalDB.userDao().update(users[0])
                }
            }
        }

        val adapter = CalendarCategoryRVAdapter(categoryItems)

        // 특정 카테고리 클릭시, 그에 맞는 일정 보여주기
        adapter.itemClick = object : CalendarCategoryRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                categoryItems.forEach { c ->
                    c.isSelected = c.name == categoryItems[position].name
                }

                binding.calendarCategoryRv.adapter!!.notifyDataSetChanged()

                // 로그인 되어있을 경우만 서버로부터 데이터 받아오기
                if (isUserLogin) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val users = CoroutineScope(Dispatchers.IO).async {
                            userLocalDB.userDao().getAll()
                        }.await()

                        updateUserSchedule(users[0].userToken!!)
                    }
                }

                val categoryId = categoryItems[position].categoryId

                if (categoryId == "-2") {
                    // 카테고리가 통합일 경우
                    binding.calendarView.setSchedules(scheduleItems)
                } else {
                    val scheduleList = mutableListOf<CalendarScheduleObject>()
                    scheduleItems.forEach { s ->
                        // 스케줄의 아이디는 organizationID와 동일하다
                        if (s.id == categoryId!!.toInt()) {
                            scheduleList.add(s)
                        }
                    }
                    binding.calendarView.setSchedules(scheduleList)
                }
            }
        }

        binding.calendarCategoryRv.adapter = adapter

        // RecyclerView 를 가로로 보이게 하기
        binding.calendarCategoryRv.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.HORIZONTAL,
            false
        )

        // 캘린더 기본 세팅
        binding.calendarView.setupDefaultCalendarSet()
        binding.calendarView.setSchedules(scheduleItems)

        // 유저 이름이 적힌 원 클릭시 이벤트
        binding.calendarHeaderUserIcon.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 업데이트 버튼 클릭시 이벤트
        binding.calendarUpdateButton.setOnClickListener {
            if (!isUserLogin) {
                Toast.makeText(binding.root.context, "로그인이 필요한 기능이에요", Toast.LENGTH_LONG).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val users = CoroutineScope(Dispatchers.IO).async {
                        userLocalDB.userDao().getAll()
                    }.await()

                    // 카테고리 & 스케줄 업데이트
                    updateUserCategory(users[0].userToken!!)
                    updateUserSchedule(users[0].userToken!!)

                    // 서버로부터 업데이트 된 내용들을 로컬이랑 맞춘다.
                    users[0].userCategory = categoryItems
                    users[0].userSchedule = scheduleItems

                    CoroutineScope(Dispatchers.IO).launch {
                        userLocalDB.userDao().update(users[0])
                    }

                    Toast.makeText(binding.root.context, "업데이트 되었어요!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 그룹 참여 버튼 클릭시 이벤트
        binding.calendarBottomGroupJoinButton.setOnClickListener {
            if (!isUserLogin) {
                Toast.makeText(binding.root.context, "로그인이 필요한 기능이에요", Toast.LENGTH_LONG).show()
            } else {
                GroupJoinPopup(this).show()
            }
        }

        // 일정 추가 버튼 클릭시 이벤트
        binding.calendarBottomAddScheduleButton.setOnClickListener {
            val intent = Intent(this, AddSchedule::class.java)
            intent.putExtra("isUserLogin", isUserLogin)
            startActivity(intent)
            finish()
        }

        // 그룹 관리 버튼 클릭시 이벤트
        binding.footerBottomGroupManagementButton.setOnClickListener {
            if (!isUserLogin) {
                Toast.makeText(binding.root.context, "로그인이 필요한 기능이에요", Toast.LENGTH_LONG).show()
            } else {
                val apiGroupListInfo = retrofit.create(APIGroupListInfo::class.java)

                CoroutineScope(Dispatchers.Main).launch {
                    val users = CoroutineScope(Dispatchers.IO).async {
                        userLocalDB.userDao().getAll()
                    }.await()
//                Log.e("token", users[0].userToken!!)
                    //users[0].userToken!!
                    apiGroupListInfo.getGroupListInfo(users[0].userToken!!
                    ).enqueue(object : Callback<GroupListInfoJSON> {
                        override fun onResponse(
                            call: Call<GroupListInfoJSON>,
                            response: Response<GroupListInfoJSON>,
                        ) {
                            getGroupManagementActivity(response.body()?.organizationInfoResponseList!!)
                        }

                        override fun onFailure(call: Call<GroupListInfoJSON>, t: Throwable) {
                            Log.d(ContentValues.TAG, "실패 : $t")
                        }
                    })
                }
            }
        }
    }

    private fun getGroupManagementActivity(organizationInfoResponseListObject:Array<GroupInfoJSON> ) {
        val intent = Intent(this, GroupManagementActivity::class.java)
        intent.putExtra("organizationInfoResponseListObject",organizationInfoResponseListObject)
        startActivity(intent)
        finish()
    }

    // 카테고리 업데이트
    private fun updateUserCategory(userToken: String) {
        val apiGetMyOrganization = retrofit.create(APIGetMyOrganization::class.java)

        apiGetMyOrganization.getMyOrganization(userToken)
            .enqueue(object : Callback<MyOrganizationJSON> {
                override fun onResponse(
                    call: Call<MyOrganizationJSON>,
                    response: Response<MyOrganizationJSON>
                ) {
                    response.body()!!.organizationInfoResponseList.forEach { organizationInfo ->
                        var flag = true
                        categoryItems.forEach { category ->
                            if (category.name == organizationInfo.title) {
                                flag = false
                            }
                        }

                        // 로컬 DB 에 없는 카테고리만 넣는다.
                        if (flag) {
                            categoryItems.add(
                                Category(
                                    isSelected = false,
                                    categoryId = organizationInfo.organizationId.toString(),
                                    color = organizationInfo.color,
                                    name = organizationInfo.title,
                                    owner = organizationInfo.presidentName
                                )
                            )
                        }
                    }

                    binding.calendarCategoryRv.adapter!!.notifyDataSetChanged()

                    // 스케줄 업데이트
                    updateUserSchedule(userToken)
                }

                override fun onFailure(call: Call<MyOrganizationJSON>, t: Throwable) {
                    Log.e("실패", "$t")
                    Toast.makeText(binding.root.context, "카테고리 업데이트에 실패했어요", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 스케줄 업데이트
    private fun updateUserSchedule(userToken: String) {
        val apiGetSchedule = retrofit.create(APIGetSchedule::class.java)
        apiGetSchedule.getMySchedule(userToken)
            .enqueue(object : Callback<GetSchedulesJSON> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<GetSchedulesJSON>,
                    response: Response<GetSchedulesJSON>,
                ) {
                    response.body()!!.scheduleDtoList.forEach { schedule ->
                        // 카테고리 색 얻어서 스케줄 추가하기
                        categoryItems.forEach { category ->
                            if (category.categoryId == schedule.id.toString()) {
                                // 스케줄 추가하기
                                scheduleItems.add(
                                    CalendarScheduleObject(
                                        id = schedule.id.toInt(),
                                        color = Color.parseColor(category.color),
                                        text = schedule.title,
                                        startDate = LocalDateTime.parse(schedule.startDate + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                        endDate = LocalDateTime.parse(schedule.endDate + "Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                        isHoliday = false
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetSchedulesJSON>, t: Throwable) {
                    Log.e(ContentValues.TAG, "실패 : $t")
                    Toast.makeText(binding.root.context, "스케줄 업데이트에 실패했어요", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

@JsonClass(generateAdapter = true)
data class Category(
    var isSelected: Boolean,
    var categoryId: String?=null,
    var color: String?=null,
    val name: String?=null,
    val owner: String?=null
)

class CalendarCategoryRVAdapter (
    val items : MutableList<Category>
) : RecyclerView.Adapter<CalendarCategoryRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CalendarCategoryRVAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.category_rv_item, parent, false)

        return ViewHolder(view)
    }

    // 클릭 이벤트를 위한 인터페이스
    interface ItemClick {
        fun onClick(view : View, position: Int) {}
    }

    var itemClick : ItemClick? = null

    override fun onBindViewHolder(holder: CalendarCategoryRVAdapter.ViewHolder, position: Int) {
        if (itemClick != null) {
            holder.itemView.setOnClickListener{
                    v -> itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Category) {
            val itemTextView = itemView.findViewById<TextView>(R.id.calendar_category_rv_item_text)
            itemTextView.text = item.name
            if (item.isSelected) {
                itemTextView.setTextColor(Color.parseColor("#C0C6FF"))
            } else {
                itemTextView.setTextColor(Color.parseColor("#000000"))
            }
        }
    }
}