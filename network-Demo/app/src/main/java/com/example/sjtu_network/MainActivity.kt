package com.example.sjtu_network

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sjtu_network.dict.JsonRootBean
import com.example.sjtu_network.dict.Synos
import com.example.sjtu_network.interceptor.TimeConsumeInterceptor
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.CacheControl
import java.util.concurrent.TimeUnit.DAYS


class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var editText: EditText? = null
    var word: String? = null
    var mcontext: Context? = null

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

    var client: OkHttpClient? = null

    val gson = GsonBuilder().create()

    var cache:Cache? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)
        editText = findViewById(R.id.edit_text)
        mcontext = applicationContext
        cache = Cache(File(mcontext?.cacheDir, "word_cache"), maxSize = 50L * 1024L * 1024L)

        client = OkHttpClient
            .Builder()
            .cache(cache)
            .addInterceptor(TimeConsumeInterceptor())
            .eventListener(okhttpListener).build()

        requestBtn?.setOnClickListener {
            showText?.text = ""
            word = editText?.text.toString()
            click()
        }
    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .cacheControl(CacheControl.Builder().maxStale(30, DAYS).build())
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client?.newCall(request)?.enqueue(callback)


    }

    fun click() {
        val url = "https://dict.youdao.com/jsonapi?q=${word}"
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val jsonRootBean = gson.fromJson(bodyString, JsonRootBean::class.java)
                if(jsonRootBean.syno == null) {
                    showText?.text = "未查询出结果，请更换单词"
                } else {
                    val synos: List<Synos> = jsonRootBean.syno.synos
                    val text: StringBuilder = StringBuilder()
                    for (index in synos.indices) {
                        text.append(synos[index].syno.tran)
                    }
                    Log.d("cacheHit:", cache?.hitCount().toString())

                    showText?.text = text
                }



            }
        })
    }
}