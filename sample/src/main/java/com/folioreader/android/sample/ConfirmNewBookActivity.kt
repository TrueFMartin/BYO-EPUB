package com.folioreader.android.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.folioreader.builder.Chapter
import com.folioreader.byobook.MainBuilderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val CREATE_FILE = 1
const val CREATE_DIR = 2
@ExperimentalCoroutinesApi
class ConfirmNewBookActivity: AppCompatActivity() {
    lateinit var tvBookFilepath: TextView
//    private val context = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    lateinit var progress: ProgressBar
    lateinit var builderManager: MainBuilderManager
    lateinit var btnSubmit: Button
    lateinit var numPickerMin: NumberPicker
    lateinit var numPickerMax: NumberPicker
    var chapters: MutableList<Chapter>? = null
    var startURL = ""
    var filePath = ""
    val waitText = "Preparing E-Book..."
    lateinit var tvPercentDone: TextView
    var numChaptersDone = 0

    lateinit var labelPickerMin: TextView
    lateinit var labelPickerMax: TextView

    var chapterMin = 0
    var chapterMax = 0
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
        builderManager = MainBuilderManager(startURL)

        numPickerMin = findViewById<NumberPicker?>(R.id.num_picker_min).also { it.setOnValueChangedListener(::minChangedListener) }
        numPickerMax = findViewById<NumberPicker?>(R.id.num_picker_max).also { it.setOnValueChangedListener(::maxChangedListener) }

        labelPickerMin = findViewById(R.id.label_num_picker_min)
        labelPickerMax = findViewById(R.id.label_num_picker_max)
        tvPercentDone = findViewById(R.id.tv_percent_complete)

        CoroutineScope(Dispatchers.Main).launch {
            setSubmitButton(false)
            setPercentVisible(false)
            setCircleProgress(true)
            setNumberPickers(false)
            setTextViewFilePath(false)

            chapters = builderManager.produceFilename()
            setNumberPickers(true)
            fillNumPickers()
            setSubmitButton(true)
            setCircleProgress(false)
            filePath = builderManager.builder.fileName
            setTextViewFilePath(true)
        }

    }

    private fun minChangedListener(numberPicker: NumberPicker?, i: Int, i1: Int) {
        chapterMin = i1
    }
    private fun maxChangedListener(numberPicker: NumberPicker?, i: Int, i1: Int) {
        chapterMax = i1
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CREATE_FILE
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("ConfirmNewBookActivity", "alter document, min: $chapterMin, max: $chapterMax")
                    val response = builderManager.alterDocument(
                        uri,
                        chapters!!.subList(chapterMin, chapterMax + 1),
                        ::setPercent,
                        applicationContext
                    )
                    if (response) {
                        tvPercentDone.text = "Complete!"
                        setCircleProgress(false)
                        Toast.makeText(applicationContext,"Complete!", Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(applicationContext,"Book failed to download", Toast.LENGTH_LONG).show()
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                }
            }
        } else if (requestCode == CREATE_DIR
            && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val min = numPickerMin.value
                val max = numPickerMax.value
                builderManager.build(uri, chapters!!.subList(min, max+1), applicationContext)
            }
        }
    }
    private fun setPercentVisible(turnOn: Boolean) {
        if (turnOn) {
            tvPercentDone.visibility = View.VISIBLE
        } else {
            tvPercentDone.visibility = View.GONE
        }
    }

    suspend fun setPercent() {
          withContext(Dispatchers.Main){
            numChaptersDone++
            val s = "Chapters Complete: $numChaptersDone/${chapterMax}"
            tvPercentDone.text = s
        }

    }
    private fun setCircleProgress(turnOn: Boolean) {
        if (turnOn)
            progress.visibility = View.VISIBLE
        else
            progress.visibility = View.GONE
    }

    private fun setSubmitButton(turnOn: Boolean) {
        if (turnOn) {
            btnSubmit.alpha = 1f
            btnSubmit.isClickable = true
        } else {
            btnSubmit.alpha = .5f
            btnSubmit.isClickable = false
        }
    }

    private fun setTextViewFilePath(turnOn: Boolean) {
        if (turnOn) {
            tvBookFilepath.text = filePath
        } else {
            tvBookFilepath.text = waitText
        }
    }

    private fun setNumberPickers(isOn: Boolean) {
        if (isOn) {
            // Number Pickers before chapters
            numPickerMin.alpha = 1f
            numPickerMax.alpha = 1f
            numPickerMin.isClickable = true
            numPickerMax.isClickable = true
        } else {
            numPickerMin.alpha = .5f
            numPickerMax.alpha = .5f
            numPickerMin.isClickable = false
            numPickerMax.isClickable = false
        }
    }

    private fun hideNumberPickers() {
        labelPickerMin.visibility = View.GONE
        labelPickerMax.visibility = View.GONE
        numPickerMin.visibility = View.GONE
        numPickerMax.visibility = View.GONE
        numPickerMin.isClickable = false
        numPickerMax.isClickable = false
    }
    private fun fillNumPickers() {
        val chapterNames = if (chapters != null)
            Array(chapters!!.size){ it: Int -> chapters!![it].title}
        else
            arrayOf("Error, no chapter links were parsed")
        numPickerMin.minValue = 0
        numPickerMin.maxValue = chapterNames.size - 1
        numPickerMin.displayedValues = chapterNames

        numPickerMax.maxValue = chapterNames.size - 1
        numPickerMax.minValue = 0
        numPickerMax.displayedValues = chapterNames
        numPickerMax.value = chapterNames.size - 1

        chapterMax = chapterNames.size - 1

    }

    private fun submitCallback() {
        hideNumberPickers()
        setPercentVisible(true)
        setCircleProgress(true)
        setSubmitButton(false)
        tvPercentDone.text = "Building chapters:"
        createFile(filePath)
    }

    private fun createFile(fileName: String) {
        val CREATE_FILE = 1
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/epub+zip"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        startActivityForResult(intent, CREATE_FILE)
    }


}
