package com.example.ccm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class GroupCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_create)

        val popupOnCancelListener = DialogInterface.OnCancelListener {
            Toast.makeText(
                this,
                "그룹 생성이 완료되었어요!",
                Toast.LENGTH_LONG
            ).show()

            // Todo : 그룹 생성 완료 이후 그룹 관리에 추가한 그룹 업데이트 해주기
            finish()
        }

        val groupCreateButton = findViewById<Button>(R.id.group_create_button)
        groupCreateButton.setOnClickListener {
            val groupCreateCodePopup = GroupCreateCodePopup(this)
            groupCreateCodePopup.setOnCancelListener(popupOnCancelListener)
            groupCreateCodePopup.show()
        }
    }
}