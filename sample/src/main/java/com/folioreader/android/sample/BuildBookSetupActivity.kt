package com.folioreader.android.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class BuildBookSetupActivity: AppCompatActivity() {
    lateinit var etEnterURL: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        etEnterURL = findViewById(R.id.et_enter_url)
        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            submitCallback()
        }

    }

    private fun submitCallback() {
        val url = etEnterURL.text
        val intent = Intent(this, )
    }
}