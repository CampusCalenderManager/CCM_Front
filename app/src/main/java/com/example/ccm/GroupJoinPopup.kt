package com.example.ccm

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccm.API.APICreateGroup
import com.example.ccm.API.APIJoinGroup
import com.example.ccm.API.JoinGroupJSON
import com.example.ccm.databinding.ActivityGroupJoinPopupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GroupJoinPopup(context: Context) : Dialog(context) {
    private lateinit var binding: ActivityGroupJoinPopupBinding

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
                Toast.makeText(context, "참여코드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                this.dismiss()
            } else {
                // Todo : 서버에 코드 검증 요청 후, 맞는 코드라면 그룹 관리에 그룹 추가하기

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://jenkins.argos.or.kr")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val apiJoinGroup = retrofit.create(APIJoinGroup::class.java)
                this.dismiss()
                CoroutineScope(Dispatchers.Main).launch {
                    val users = CoroutineScope(Dispatchers.IO).async {
                        CCMApp.userLocalDB.userDao().getAll()
                    }.await()
                    apiJoinGroup.postJoinGroup(users[0].userToken!!,
                        JoinGroupJSON(
                            groupJoinCode.toString()
                        )
                    ).enqueue(object : Callback<Void> {
                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>,
                        ) {
                            Log.e(ContentValues.TAG, "성공 : ${response.raw()} ${response.message()}")
                            if (response.code() == 200) {
                                Toast.makeText(context, "그룹 참여가 완료되었습니다.", Toast.LENGTH_LONG).show()
                            } else if (response.code() == 409) {
                                Toast.makeText(context, "이미 가입한 그룹의 참여코드입니다.", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(context, "참여 코드가 유효하지 않습니다.", Toast.LENGTH_LONG)
                                    .show()
                            }

                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e(ContentValues.TAG, "실패 : $t")
                        }
                    })
                }
            }
        }
    }
}