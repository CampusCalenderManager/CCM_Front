package com.example.ccm

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.ccm.API.APISignIn
import com.example.ccm.API.SignInJSON
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val userNameInput = findViewById<EditText>(R.id.login_id_input)
            val passwordInput = findViewById<EditText>(R.id.login_password_input)

            callSignInAPI(userNameInput.text.toString(), passwordInput.text.toString())
        }
    }

    private fun callSignInAPI(
        userName: String,
        password: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiAddSchedule = retrofit.create(APISignIn::class.java)

        apiAddSchedule.postSignIn(
            SignInJSON (
                userName,
                password
            )
        ).enqueue(object : Callback<SignInJSON> {
            override fun onResponse(
                call: Call<SignInJSON>,
                response: Response<SignInJSON>,
            ) {
                Log.d(ContentValues.TAG, "성공 : ${response.raw()} ${response.message()}")
            }

            override fun onFailure(call: Call<SignInJSON>, t: Throwable) {
                Log.d(ContentValues.TAG, "실패 : $t")
            }
        })
    }
}