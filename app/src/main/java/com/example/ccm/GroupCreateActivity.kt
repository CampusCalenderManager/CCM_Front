package com.example.ccm

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.ccm.API.APICreateGroup
import com.example.ccm.API.CreateGroupCode
import com.example.ccm.API.CreateGroupJSON
import com.example.ccm.CCMApp.Companion.userLocalDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

var participationCode = ""

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
        }

        val groupCreateButton = findViewById<Button>(R.id.group_create_button)
        groupCreateButton.setOnClickListener {

            val groupName = findViewById<EditText>(R.id.group_create_name_input)

            val groupDiscription = findViewById<EditText>(R.id.group_create_description_input)


            val retrofit = Retrofit.Builder()
                .baseUrl("http://jenkins.argos.or.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiCreateGroup = retrofit.create(APICreateGroup::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                val users = CoroutineScope(Dispatchers.IO).async {
                    userLocalDB.userDao().getAll()
                }.await()
//                Log.e("token", users[0].userToken!!)
                //users[0].userToken!!
                apiCreateGroup.postCreateGroup(users[0].userToken!!,
                    CreateGroupJSON(
                        groupName.text.toString(),
                        groupDiscription.text.toString()
                    )
                ).enqueue(object : Callback<CreateGroupCode> {
                    override fun onResponse(
                        call: Call<CreateGroupCode>,
                        response: Response<CreateGroupCode>,
                    ) {
                        Log.d("login POST", "${response.body()}")
                        createCodePopup(response.body()?.participationCodeResponse.toString())
                        Log.d(ContentValues.TAG, "성공 : ${response.raw()} ${response.message()}")
                    }

                    private fun createCodePopup(code: String) {
                        setParticipationCode(code)
                        val groupCreateCodePopup = GroupCreateCodePopup(this@GroupCreateActivity)
                        groupCreateCodePopup.setOnCancelListener(popupOnCancelListener)
                        groupCreateCodePopup.show()
                    }

                    override fun onFailure(call: Call<CreateGroupCode>, t: Throwable) {
                        Log.d(ContentValues.TAG, "실패 : $t")
                    }
                })
            }



        }
    }

    private fun setParticipationCode(code: String) {
        participationCode = code
    }
    fun getParticipationCode(): String {
        return participationCode
    }



}