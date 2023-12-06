package com.folioreader.android.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BuildBookSetupActivity: AppCompatActivity() {
     val CODE_BACK_TO_SETUP: Int = 1
    lateinit var etEnterURL: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newbook)
        etEnterURL = findViewById(R.id.et_enter_url)
        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            submitCallback()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CODE_BACK_TO_SETUP && resultCode == RESULT_OK) {
            finish()
        }

    }
    private fun submitCallback() {
        var url = etEnterURL.text.toString()
        // FIXME FOR TESTING
        url = "https://www.wanderinginn.com/table-of-contents"
        val i = Intent(
            applicationContext,
            ConfirmNewBookActivity::class.java
        )
        i.putExtra("EXTRA_URL", url)
        startActivityForResult(i, CODE_BACK_TO_SETUP)
    }
}