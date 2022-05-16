package com.example.ccm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupManagementActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_management)

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
    }
}