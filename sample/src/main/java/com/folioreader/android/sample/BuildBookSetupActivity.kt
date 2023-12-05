package com.folioreader.android.sample

import android.app.Application
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import com.folioreader.byobook.MainBuilder
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.Executors
import kotlinx.coroutines.runBlocking


class BuildBookSetupActivity: AppCompatActivity() {
    lateinit var etEnterURL: EditText
    private val context = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    private var mainBuilder = MainBuilder("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newbook)
        etEnterURL = findViewById(R.id.et_enter_url)
        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            submitCallback()
        }
    }

    private fun demoWithRunBlocking() = runBlocking {

            launch(context) {
                runBlocking {
                    mainBuilder.run()
                }
            }

    }
    private fun submitCallback() {
        val url = etEnterURL.text
        mainBuilder = MainBuilder(url.toString())
        demoWithRunBlocking()
    }
}