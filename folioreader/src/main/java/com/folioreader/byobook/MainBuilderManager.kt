package com.folioreader.byobook

import android.content.Context
import android.net.Uri
import com.folioreader.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@ExperimentalCoroutinesApi
class MainBuilderManager(val url: String) {

    val builder = MainBuilder(url)
    suspend fun produceFilename(): String = withContext(Dispatchers.IO) {
        return@withContext builder.run()
    }


    fun build(path: Uri, applicationContext: Context) {
        val s = PathFinder(applicationContext).getPath(path)
        builder.build(s)
    }

    fun buildDir(path: Uri, applicationContext: Context, fileName: String) {

        val s = PathFinder(applicationContext).getPath(path)
        builder.build(s)
    }

     fun alterDocument(uri: Uri, applicationContext: Context) {
        try {
            val stream1 = applicationContext.resources.openRawResource(R.raw.epub_style_sheet)
            builder.addStyleSheet(stream1, "epub.css")
//            val stream2 = applicationContext.resources.openRawResource(R.raw.FreeSansBold)
//            builder.addStyleSheet(stream2, "FreeSansBold.otf")
//            val stream3 = applicationContext.resources.openRawResource(R.raw.FreeSerif)
//            builder.addStyleSheet(stream3, "FreeSansBold.otf")
//            val stream4 = applicationContext.resources.openRawResource(R.raw.UbuntuMonoB)
//            builder.addStyleSheet(stream4, "FreeSansBold.otf")
//            val stream5 = applicationContext.resources.openRawResource(R.raw.UbuntuMonoBI)
//            builder.addStyleSheet(stream5, "FreeSansBold.otf")
//            val stream6 = applicationContext.resources.openRawResource(R.raw.UbuntuMonoR)
//            builder.addStyleSheet(stream6, "FreeSansBold.otf")
//            val stream7 = applicationContext.resources.openRawResource(R.raw.UbuntuMonoRI)
//            builder.addStyleSheet(stream7, "FreeSansBold.otf")


            val stream = FileOutputStream(applicationContext.contentResolver.openFileDescriptor(uri, "w")?.fileDescriptor)
            builder.build(stream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
//-----------Kotlin version of builder if I make make it concurrent--------
//    fun run(): String? {
//        val contentFetcher = ContentFetcher()
//        val mainPage: ContentFetcher.ParseResult
//        mainPage = try {
//            contentFetcher.fetchContentInitial(url)
//        } catch (e: IOException) {
//            throw RuntimeException("failed to get initial webpage$e")
//        }
//        val title = mainPage.parser.extractTitle(mainPage.document)
//        var author = mainPage.parser.extractAuthor(mainPage.document)
//        val chapters =
//            mainPage.parser.getChapterUrls(mainPage.document)
//        book = BookBuilder(title)
//        if (author == null || author.isEmpty()) author = title
//        val splitAuthor =
//            author.split("[, ]".toRegex()).dropLastWhile { it.isEmpty() }
//                .toTypedArray()
//        if (splitAuthor.size < 1) book.setAuthor("Undefined") else if (splitAuthor.size < 2) book.setAuthor(
//            splitAuthor[0]
//        ) else if (splitAuthor.size < 3) book.setAuthor(
//            splitAuthor[0],
//            splitAuthor[1]
//        ) else book.setAuthor(splitAuthor[0], splitAuthor[splitAuthor.size - 1])
//        var i = 0
//        for ((sourceUrl, title1): Chapter in chapters) {
//            i++
//            // FIXME remove after testing
//            if (i > 2) {
//                Log.e("MainBuilder", "Ending chapter load because greater than 2")
//                break
//            }
//            val chapterPage: ContentFetcher.ParseResult
//            chapterPage = try {
//                contentFetcher.fetchContent(sourceUrl)
//            } catch (e: IOException) {
//                throw RuntimeException("failed to get chapter webpage $sourceUrl$e")
//            }
//            mainPage.parser.removeUnusedElementsToReduceMemoryConsumption(chapterPage.document)
//            val chapterContent: Element = mainPage.parser.findContent(chapterPage.document)
//            mainPage.parser.removeUnwantedElementsFromContentElement(chapterContent)
//            mainPage.parser.addTitleToContent(chapterPage.document, chapterContent)
//            book.addChapter(title1, chapterContent.text())
//        }
//        return mainPage.parser.makeSaveAsFileNameWithoutExtension(title, false)
//    }
}