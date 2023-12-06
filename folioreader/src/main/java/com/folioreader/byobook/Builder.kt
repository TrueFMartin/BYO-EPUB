package com.folioreader.byobook

import android.util.Log
import androidx.lifecycle.ViewModel
import com.folioreader.builder.Chapter
import com.folioreader.builder.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Entities
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.KSuspendFunction0

class Builder(val url: String): ViewModel() {
    var book: BookBuilder? = null
    var contentFetcher: ContentFetcher? = null
    private var mainPage: ContentFetcher.ParseResult? = null
    var workerPool: ExecutorService? = null
    var title: String? = null
    var author: String? = null
    var fileName = ""

    fun runInit(): MutableList<Chapter>? {
        contentFetcher = ContentFetcher()
        mainPage = try {
            contentFetcher!!.fetchContentInitial(url)
        } catch (e: IOException) {
            throw RuntimeException("failed to get initial webpage$e")
        }
        title = mainPage?.parser?.extractTitle(mainPage?.document)
        author = mainPage?.parser?.extractAuthor(mainPage?.document)
        fileName = mainPage?.parser?.makeSaveAsFileNameWithoutExtension(title, false) + ".epub"
        return mainPage?.parser?.getChapterUrls(mainPage?.document)
    }

    // Create an interface to update UI counter with the result after processing chapter
    interface OnProcessedListener {
        fun onProcessed()
    }
    @Throws(RuntimeException::class)
    suspend fun runCollectChapters(chapters: List<Chapter>, processUI: KSuspendFunction0<Unit>):Boolean = withContext(Dispatchers.IO){
        try {
            book = BookBuilder(title)
            if (author == null || author!!.isEmpty()) author = title
            val splitAuthor = author!!.split("[, ]".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (splitAuthor.isEmpty()) book!!.setAuthor("Undefined") else if (splitAuthor.size < 2) book!!.setAuthor(
                splitAuthor[0]
            ) else if (splitAuthor.size < 3) book!!.setAuthor(
                splitAuthor[0],
                splitAuthor[1]
            ) else book!!.setAuthor(
                splitAuthor[0], splitAuthor[splitAuthor.size - 1]
            )
//            val mainScope = CoroutineScope(SupervisorJob(coroutineContext.job))
            workerPool = Executors.newFixedThreadPool(minOf(Runtime.getRuntime().availableProcessors(), chapters.size))
            val list: List<Task> = List(chapters.size){ Task(mainPage!!, contentFetcher!!,
                chapters[it]
            )}
            val futures = workerPool!!.invokeAll(list)
            workerPool!!.shutdown()

            for(i in futures.indices) {
                val content = futures[i].get()
                book!!.addChapter(chapters[i].title, toXHTML(futures[i].get().html(), chapters[i].title))
                processUI()
            }
        } catch (e: Exception) {
            workerPool!!.shutdown()
            return@withContext false
        }
        return@withContext true
    }

    class Task(val mainPage: ContentFetcher.ParseResult, val contentFetcher: ContentFetcher, val chapter: Chapter): Callable<Element> {
        override fun call(): Element {
            val chapterPage: ContentFetcher.ParseResult = try {
                contentFetcher!!.fetchContent(chapter.sourceUrl)
            } catch (e: IOException) {
                throw RuntimeException("failed to get chapter webpage $chapter.sourceUrl $e")
            }
            mainPage!!.parser.removeUnusedElementsToReduceMemoryConsumption(chapterPage.document)
            val chapterContent: Element = mainPage!!.parser.findContent(chapterPage.document)
            mainPage!!.parser.removeUnwantedElementsFromContentElement(chapterContent)
            mainPage!!.parser.addTitleToContent(chapterPage.document, chapterContent)
            return chapterContent
        }
    }
    private fun toXHTML(html: String, title: String): String? {
        val document = Jsoup.parse(html)
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml)
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        addHeadElements(document, title)
        document.getElementsByTag("html").attr("xmlns", "http://www.w3.org/1999/xhtml")
        val sb = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
        sb.append("<!DOCTYPE html>\n")
        return sb.append(document.html()).toString()
    }

    private fun addHeadElements(doc: Document, title: String) {
        val style = Element("link", Util.XMLNS)
        doc.head().appendChild(style)
        style.attr("href", "epub.css")
        style.attr("type", "text/css")
        style.attr("rel", "stylesheet")
        val titleElem = Element("title", Util.XMLNS)
        doc.head().appendChild(titleElem)
        titleElem.text(title)
    }

    fun addStyleSheet(resourceStream: InputStream, href: String?) {
        book!!.addOtherResource(resourceStream, href)
    }

    fun build(path: String?) {
        book!!.build(path)
    }

    fun build(stream: FileOutputStream?) {
        book!!.build(stream)
    }

}