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

        binding.loginCancleButton.setOnClickListener {
            val intent = Intent(binding.root.context, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginBackButton.setOnClickListener {
            val intent = Intent(binding.root.context, MainActivity::class.java)
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
                // ???????????? ??????????????? ????????? ????????? ??????
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
                            Category(false, "-1", "#59bfff", "??????", userData!!.name),
                        )
                        users[0].userSchedule = listOf()

                        CoroutineScope(Dispatchers.IO).async {
                            userLocalDB.userDao().update(users[0])
                        }.await()

                        Toast.makeText(binding.root.context, "??????????????? ${users[0].username} ???", Toast.LENGTH_LONG).show()

                        val intent = Intent(binding.root.context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // response code ??? 200??? ???????????? ?????? ????????? ??????
                    Toast.makeText(binding.root.context, "???????????? ???????????????", Toast.LENGTH_LONG).show()

                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<LoginUserDataJSON>, t: Throwable) {
                Log.d(ContentValues.TAG, "?????? : $t")
                Toast.makeText(binding.root.context, "????????? ???????????????", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ???????????? ????????? ?????? ???????????? ??????
    override fun onBackPressed() {
        val intent = Intent(binding.root.context, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}