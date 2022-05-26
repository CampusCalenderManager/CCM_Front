package com.example.ccm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccm.API.GroupInfoJSON

class GroupManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_management)

        // Todo : 서버에서 사용자의 그룹 리스트를 받아서 보여주기
        val intent = intent
        val organizationInfoResponseListObject: Array<GroupInfoJSON>? = intent.getSerializableExtra("organizationInfoResponseListObject") as Array<GroupInfoJSON>?
        val group = mutableListOf<GroupCard>()

        val organizationInfoResponseListObjectSize = organizationInfoResponseListObject?.size

        if (organizationInfoResponseListObjectSize != null) {
            for (i in 1..organizationInfoResponseListObjectSize.toInt())
                group.add(
                    GroupCard(
                        organizationInfoResponseListObject[i-1].title,
                        organizationInfoResponseListObject[i-1].description,
                    )
                )
        }

        // Todo : 리스트 아이템 클릭시 해당하는 그룹의 상세 페이지 보이기





        val groupView = findViewById<RecyclerView>(R.id.group_view)

        val groupViewAdapter = GroupRecyclerViewAdapter(group)

        groupView.adapter = groupViewAdapter

        groupView.layoutManager = LinearLayoutManager(applicationContext)


        // 그룹 참여 팝업 띄우기
        val groupJoinButton = findViewById<ImageButton>(R.id.footer_button_join_group)

        groupJoinButton.setOnClickListener {
            GroupJoinPopup(this).show()
        }

        val groupCreateButton = findViewById<ImageButton>(R.id.footer_create_group_button)
        groupCreateButton.setOnClickListener {
            val intent = Intent(this, GroupCreateActivity::class.java)
            startActivity(intent)
        }

    }
}