package com.example.ccm

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ccm.API.ApiSignUp
import com.example.ccm.API.SignUpJSON
import com.example.ccm.databinding.ActivitySignUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 취소 버튼
        binding.signUpFooterCancelButton.setOnClickListener {
            val intent = Intent(binding.root.context, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 회원가입 버튼
        binding.signUpButton.setOnClickListener {
            val signUpName = binding.signUpNameInput.text.toString()
            val signUpUsername = binding.signUpIdInput.text.toString()
            val signUpPassword = binding.signUpPasswordInput.text.toString()

            callPostSignUpApi(signUpName, signUpUsername, signUpPassword)
        }
    }

    private fun callPostSignUpApi(
        signUpName: String,
        signUpUsername: String,
        signUpPassword: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://jenkins.argos.or.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiAddSchedule = retrofit.create(ApiSignUp::class.java)

        apiAddSchedule.postSignUp(
            SignUpJSON(signUpName, signUpUsername, signUpPassword)
        ).enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                if (response.code() == 201) {
                    Toast.makeText(binding.root.context, "회원가입이 완료되었어요!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(binding.root.context, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(binding.root.context, "회원가입에 실패했어요", Toast.LENGTH_SHORT).show()
                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
                Toast.makeText(binding.root.context, "요청이 잘못됐어요", Toast.LENGTH_LONG).show()
            }
        })
    }

    // 뒤로가기 누르면 로그인 액티비티 생성
    override fun onBackPressed() {
        val intent = Intent(binding.root.context, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}



