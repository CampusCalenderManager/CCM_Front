package com.example.ccm

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.example.ccm.databinding.ActivityGroupJoinPopupBinding


class GroupJoinPopupActivity(context : Context) : Dialog(context) {
    private lateinit var binding : ActivityGroupJoinPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupJoinPopupBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_group_join_popup)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}