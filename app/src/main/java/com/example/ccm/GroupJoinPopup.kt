package com.example.ccm

import android.app.Dialog
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


class GroupJoinPopup(context: Context) : Dialog(context) {
    private lateinit var binding : ActivityGroupJoinPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupJoinPopupBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_group_join_popup)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val groupJoinWithCodeButton = findViewById<Button>(R.id.group_join_with_code_button)

        groupJoinWithCodeButton.setOnClickListener {
            val groupJoinCodeInput = findViewById<EditText>(R.id.group_join_code_input)
            val groupJoinCode = groupJoinCodeInput.text

            if (groupJoinCode.isNullOrBlank()) {
                Toast.makeText(context, "코드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                this.dismiss()
            } else {
                // Todo : 서버에 코드 검증 요청 후, 맞는 코드라면 그룹 관리에 그룹 추가하기

                Toast.makeText(context, groupJoinCode.toString(), Toast.LENGTH_LONG).show()
                this.dismiss()
            }
        }
    }
}