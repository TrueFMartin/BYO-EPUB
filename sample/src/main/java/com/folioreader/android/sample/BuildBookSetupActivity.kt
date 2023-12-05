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
        setContentView(R.layout.activity_newbook)
        etEnterURL = findViewById(R.id.et_enter_url)
        findViewById<Button>(R.id.btn_submit).setOnClickListener {
            submitCallback()
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
        startActivity(i)
    }
}