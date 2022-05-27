package com.example.ccm

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ccm.API.APISignIn
import com.example.ccm.API.LoginUserDataJSON
import com.example.ccm.API.SignInJSON
import com.example.ccm.CCMApp.Companion.userLocalDB
import com.example.ccm.LocalDB.User
import com.example.ccm.databinding.ActivityMainBinding
import com.example.ccm.databinding.ActivitySignInBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val userNameInput = findViewById<EditText>(R.id.login_id_input)
            val passwordInput = findViewById<EditText>(R.id.login_password_input)
            callSignInAPI(userNameInput.text.toString(), passwordInput.text.toString())
        }

        binding.loginSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun callSignInAPI(
        userName: String,
        password: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiAddSchedule = retrofit.create(APISignIn::class.java)

        apiAddSchedule.postSignIn(
            SignInJSON (userName, password)
        ).enqueue(object : Callback<LoginUserDataJSON> {
            override fun onResponse(
                call: Call<LoginUserDataJSON>,
                response: Response<LoginUserDataJSON>
            ) {
                // 로그인이 정상적으로 됐다면 토큰을 저장
                if (response.code() == 200) {
                    val userToken = response.headers().values("AccessToken")[0]
                    val userData = response.body()

                    CoroutineScope(Dispatchers.Main).launch {
                        val users = CoroutineScope(Dispatchers.IO).async {
                            userLocalDB.userDao().getAll()
                        }.await()

                        users[0].userToken = userToken
                        users[0].username = userData!!.name
                        users[0].userCategory = listOf(
                            Category(false, "-1", "#59bfff", "개인", userData!!.name),
                        )
                        users[0].userSchedule = listOf()

                        CoroutineScope(Dispatchers.IO).async {
                            userLocalDB.userDao().update(users[0])
                        }.await()

                        Toast.makeText(binding.root.context, "반갑습니다 ${users[0].username} 님", Toast.LENGTH_LONG).show()

                        val intent = Intent(binding.root.context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // response code 가 200이 아니라면 실패 메시지 출력
                    Toast.makeText(binding.root.context, "로그인에 실패했어요", Toast.LENGTH_LONG).show()

                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginUserDataJSON>, t: Throwable) {
                Log.d(ContentValues.TAG, "실패 : $t")
                Toast.makeText(binding.root.context, "요청이 잘못됐어요", Toast.LENGTH_LONG).show()
            }
        })
    }

    // 뒤로가기 누르면 메인 액티비티 생성
    override fun onBackPressed() {
        val intent = Intent(binding.root.context, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}