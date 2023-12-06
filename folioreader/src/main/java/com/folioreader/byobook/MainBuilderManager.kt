package com.folioreader.byobook

import android.content.Context
import android.net.Uri
import android.util.Log
import com.folioreader.R
import com.folioreader.builder.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.reflect.KSuspendFunction0


@ExperimentalCoroutinesApi
class MainBuilderManager(val url: String) {

    val builder = Builder(url)
    suspend fun produceFilename(): MutableList<Chapter>? = withContext(Dispatchers.IO) {
        return@withContext builder.runInit()
    }

    fun build(path: Uri, applicationContext1: MutableList<Chapter>, applicationContext: Context) {
        val s = PathFinder(applicationContext).getPath(path)
        builder.build(s)
    }


     suspend fun alterDocument(
         uri: Uri,
         chapters: MutableList<Chapter>,
         onProcessCallback: KSuspendFunction0<Unit>,
         applicationContext: Context
     ): Boolean {
        try {
            val response = builder.runCollectChapters(chapters, onProcessCallback )
            if (!response)
                return false
            val stream1 = applicationContext.resources.openRawResource(R.raw.epub_style_sheet)
            Log.e("BuilderManager", "this should be after all chapters have been parsed and added")
            builder.addStyleSheet(stream1, "epub.css")
            val stream = FileOutputStream(applicationContext.contentResolver.openFileDescriptor(uri, "w")?.fileDescriptor)
            builder.build(stream)
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

}