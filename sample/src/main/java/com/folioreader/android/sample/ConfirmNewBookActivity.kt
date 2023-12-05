package com.folioreader.android.sample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.folioreader.byobook.MainBuilderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

const val CREATE_FILE = 1
const val CREATE_DIR = 2
@ExperimentalCoroutinesApi
class ConfirmNewBookActivity: AppCompatActivity() {
    lateinit var tvBookFilepath: TextView
//    private val context = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    lateinit var progress: ProgressBar
    lateinit var builder: MainBuilderManager
    lateinit var btnSubmit: Button
    var startURL = ""
    var filePath = ""
    val waitText = "Preparing E-Book..."
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_newbook)
        tvBookFilepath = findViewById(R.id.tv_file_name)
        btnSubmit = findViewById<Button>(R.id.btn_confirm_book_path).also {
            it.setOnClickListener {
                submitCallback()
            }
        }
        findViewById<Button>(R.id.btn_cancel_book_path).setOnClickListener {
            finishActivity(-1)
        }
        progress = findViewById(R.id.progress_bar_new_book)
        startURL = intent.getStringExtra("EXTRA_URL").toString()
        builder = MainBuilderManager(startURL)

        CoroutineScope(Dispatchers.Main).launch {
            setIsFinished(false)
            filePath = builder.produceFilename()
            setIsFinished(true)
        }

    }

    fun openDirectory() {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CREATE_FILE
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                builder.alterDocument(uri, applicationContext)
            }
        } else if (requestCode == CREATE_DIR
            && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                builder.build(uri, applicationContext)
            }
        }
    }

    private fun setIsFinished(isFinished: Boolean) {
        btnSubmit.isClickable = isFinished
        if (isFinished) {
            progress.visibility = View.GONE
            tvBookFilepath.text = filePath
            btnSubmit.alpha = 1f
        } else {
            tvBookFilepath.text = waitText
            btnSubmit.alpha = .5f
        }
    }

//    private fun demoWithRunBlocking() = runBlocking {
//        builder.
//            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
//
//                    val x = builder.run { r }
//                    mainBuilder.run()
//
//            }
//
//    }
    private fun submitCallback() {
        Log.d("ConfirmNewBookActivity", "Submitting file name $filePath")
        createFile(filePath)
    }

    // Request code for creating a PDF document.

    private fun createFile(fileName: String) {
        val CREATE_FILE = 1
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/epub+zip"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    // Create file with an initial URI
    private fun createFile(pickerInitialUri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/epub+zip"
            putExtra(Intent.EXTRA_TITLE, fileName)

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
        }
        startActivityForResult(intent, CREATE_FILE)
    }
}
