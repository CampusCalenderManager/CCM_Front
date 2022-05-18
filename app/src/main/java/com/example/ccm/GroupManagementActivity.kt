package com.example.ccm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_management)

        // Todo : 서버에서 사용자의 그룹 리스트를 받아서 보여주기
        // Todo : 리스트 아이템 클릭시 해당하는 그룹의 상세 페이지 보이기
        val items = mutableListOf<GroupCard>()

        items.add(
            GroupCard(
                "자료구조(01분반)",
                "강지훈 교수의 지리는 수업입니다",
                10,
                15
            )
        )

        items.add(
            GroupCard(
                "확률과 통계(01분반)",
                "조승범 교수의 지리는 수업입니다",
                7,
                15
            )
        )

        items.add(
            GroupCard(
                "자료구조(01분반)",
                "강지훈 교수의 지리는 수업입니다",
                8,
                15
            )
        )

        items.add(
            GroupCard(
                "확률과 통계(01분반)",
                "조승범 교수의 지리는 수업입니다",
                2,
                15
            )
        )

        val groupView = findViewById<RecyclerView>(R.id.group_view)

        val groupViewAdapter = GroupRecyclerViewAdapter(items)

        groupView.adapter = groupViewAdapter
        groupView.layoutManager = LinearLayoutManager(this)

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