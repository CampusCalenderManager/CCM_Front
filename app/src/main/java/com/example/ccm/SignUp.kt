package com.example.ccm

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val signUpName = findViewById<EditText>(R.id.signUpName)
            val signUpUsername = findViewById<EditText>(R.id.signUpUsername)
            val signUpPassword = findViewById<EditText>(R.id.signUpPassword)
            callPostSignUpApi(signUpName, signUpUsername, signUpPassword)
        }
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }

    private fun callPostSignUpApi(
        signUpName: EditText,
        signUpUsername: EditText,
        signUpPassword: EditText,
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jenkins.argos.or.kr")
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiAddSchedule = retrofit.create(ApiSignUp::class.java)

        apiAddSchedule.postSignUp("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTY3OTAzOTIyMSwibWVtYmVySWQiOjF9.IkyEfwU8EiiNB9-zGKFdGOQ2N8F20c-jjXUCQAbWq0kUUS75gxUGGPXwpqA-ml5q9eYejcQ_CaelzTwpx2faqw",
            SignUpJson(
                signUpName.text.toString(),
                signUpUsername.text.toString(),
                signUpPassword.text.toString(),
            )
        ).enqueue(object : Callback<SignUpJson> {
            override fun onResponse(
                call: Call<SignUpJson>,
                response: Response<SignUpJson>,
            ) {
                Log.d(TAG, "성공 : ${response.raw()} ${response}")
            }

            override fun onFailure(call: Call<SignUpJson>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })
    }
}



