package com.example.sjtu_network

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sjtu_network.api.DoubanBean
import com.example.sjtu_network.api.DoubanService
import okhttp3.*
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null

    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            showText?.text = showText?.text.toString() + "\nDns Search:" + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = showText?.text.toString() + "\nResponse Start"
        }
    }


    val client: OkHttpClient = OkHttpClient.Builder()
        .eventListener(okhttpListener)
        .build()

    var retrofit = Retrofit.Builder()
        .baseUrl("https://movie.querydata.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)

        requestBtn?.setOnClickListener {
            click()
        }
    }

    fun request(callback: retrofit2.Callback<DoubanBean>) {
        try {
            val doubanService = retrofit.create(DoubanService::class.java)
            doubanService.getMovieInfo(25845392).enqueue(callback)
        }catch (error:Throwable) {
            error.printStackTrace()
        }
    }

    fun click() {
        request(object : Callback<DoubanBean> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: retrofit2.Call<DoubanBean>,
                response: Response<DoubanBean>
            ) {
                if (response.isSuccessful) {
                    val doubanBean = response.body()
                    showText?.text = "${showText?.text.toString()} \n\n\n" +
                            "Originalname: ${doubanBean?.originalName} \n" +
                            "Alias: ${doubanBean?.alias}  "
                } else {
                    showText?.text = "failed code is :${response.code()}"
                }

            }

            override fun onFailure(call: retrofit2.Call<DoubanBean>, t: Throwable) {
                showText?.text = t.message
            }
        })
    }


}