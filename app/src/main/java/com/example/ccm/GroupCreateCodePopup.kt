package com.example.ccm

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccm.databinding.ActivityGroupJoinPopupBinding
import android.content.ClipboardManager
import android.graphics.drawable.Drawable
import android.widget.TextView


class GroupCreateCodePopup(context: Context) : Dialog(context) {
    private lateinit var binding : ActivityGroupJoinPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupJoinPopupBinding.inflate(layoutInflater)
        setContentView(R.layout.group_create_code_popup)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Todo : 서버에서 그룹 참여 코드를 받아서 팝업내 TextView 에 띄우기
        val groupJoinCode = findViewById<TextView>(R.id.group_join_code)
        groupJoinCode.text = GroupCreateActivity().getParticipationCode()

        val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

        val groupJoinCodeCopyButton = findViewById<Button>(R.id.group_join_code_copy_button)
        groupJoinCodeCopyButton.setOnClickListener {
            val groupJoinCode = findViewById<TextView>(R.id.group_join_code)
            val clip = ClipData.newPlainText("group code", groupJoinCode.text)
            clipboard.setPrimaryClip(clip)

            groupJoinCodeCopyButton.setBackgroundResource(R.drawable.light_purple_group_join_button_top_bottom_right_radius_20dp)
            groupJoinCodeCopyButton.text = "복사완료!"
            groupJoinCode.setTextColor(Color.parseColor("#8E8E8E"))
        }
    }
}