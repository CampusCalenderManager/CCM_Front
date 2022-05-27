package com.example.ccm

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
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import com.example.ccm.CCMApp.Companion.userLocalDB
import com.example.ccm.LocalDB.User
import com.example.ccm.databinding.ActivityMainBinding
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    // Todo : 서버에서 데이터 받아서 카테고리 표시하기
    val categoryItems = mutableListOf<Category>()

    // 스케쥴로 들어가는 아이템들은 카테고리의 위치와 같아야함.
    val scheduleItems = mutableListOf<CalendarScheduleObject>()

    var isUserLogin = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로컬 DB 는 비동기로 호출해야한다. 따라서 코루틴을 사용한다.
        CoroutineScope(Dispatchers.Main).launch {
            // 먼저 유저 목록을 불러온다.
            // 사용자는 하나이므로 리스트의 첫번째를 사용하면 된다.
            val users = CoroutineScope(Dispatchers.IO).async {
                userLocalDB.userDao().getAll()
            }.await()

            if (users.isEmpty()) {
                // 앱을 처음 사용한다면 user 라는 이름으로 로컬 db에 생성
                // 토큰이 없으므로 일정을 공유하지도 그룹에 참여하지도 못한다.
                val categoryListBase = listOf(
                    Category(true, "-2", null, "통합"),
                    Category(false, "-1", "#59bfff", "개인"),
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

                categoryItems.add(Category(true, "-2", null, "통합"))
                users[0].userCategory?.forEach { category ->
                    if (category.name != "통합") {
                        categoryItems.add(category)
                    }
                }
                Log.e("category", users[0].userCategory!!.toString())
                binding.calendarCategoryRv.adapter!!.notifyDataSetChanged()

                users[0].userSchedule?.forEach { schedule ->
                    scheduleItems.add(schedule)
                }
                binding.calendarView.setSchedules(scheduleItems)

                binding.calendarHeaderUserIcon.text = users[0].username

                // 토큰의 존재 여부를 통해 로그인 되었는지를 판단한다.
                // Todo : 만약 토큰이 만료되었다면 다른 화면으로 가기 전에 로그인으로 돌려보내도록 하자.
                isUserLogin = users[0].userToken != null
            }
        }

        if (isUserLogin) {
            // Todo : 서버에서 카테고리와 스케줄을 받아온다.
            // Todo : 서버에서 받아온 데이터들을 로컬에도 넣어준다.
        }

        val adapter = CalendarCategoryRVAdapter(categoryItems)

        // 특정 카테고리 클릭시, 그에 맞는 일정 보여주기
        // Todo : 업데이트 됐을지도 모르기 때문에 특정 버튼 누를때마다 스케줄 받기
        adapter.itemClick = object : CalendarCategoryRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                categoryItems.forEach { c ->
                    c.isSelected = c.name == categoryItems[position].name
                }

                binding.calendarCategoryRv.adapter!!.notifyDataSetChanged()

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

        binding.calendarHeaderUserIcon.setOnClickListener {
            if (isUserLogin) {
                Toast.makeText(binding.root.context, "이미 로그인 되었어요!", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.calendarUpdateButton.setOnClickListener {
            // Todo : 버튼 누르면 서버에서 데이터 받아서 표시
            // Todo : 받은 데이터 로컬에 다시 저장
            isUserLogin = false
            CoroutineScope(Dispatchers.Main).launch {
                // 먼저 유저 목록을 불러온다.
                // 사용자는 하나이므로 리스트의 첫번째를 사용하면 된다.
                val users = CoroutineScope(Dispatchers.IO).async {
                    userLocalDB.userDao().getAll()
                }.await()


                CoroutineScope(Dispatchers.IO).async {
                    userLocalDB.userDao().delete(users[0])
                }.await()
            }
        }

        binding.calendarBottomGroupJoinButton.setOnClickListener {
            GroupJoinPopup(this).show()
        }

        // Todo : 하단에 있는 뒤로가기 버튼을 누르면 Main 으로 가도록 만들기 (?)
        binding.calendarBottomAddScheduleButton.setOnClickListener {
            val intent = Intent(this, AddSchedule::class.java)
            startActivity(intent)
            // finish() // 특별한 상황이 아니라면 항상 Activity 를 끝내준다.
        }

        binding.footerBottomGroupManagementButton.setOnClickListener {
            val intent = Intent(this, GroupManagementActivity::class.java)
            startActivity(intent)
            // finish() // 특별한 상황이 아니라면 항상 Activity 를 끝내준다. intent 는 쌓이기 때문.
        }
    }
}

@JsonClass(generateAdapter = true)
data class Category(
    var isSelected: Boolean,
    var categoryId: String?=null,
    var color: String?=null,
    val name: String?=null
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