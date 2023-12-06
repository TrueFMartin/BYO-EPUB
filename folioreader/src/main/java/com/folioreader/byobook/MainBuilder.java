package com.folioreader.byobook;

import android.content.Context;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Util;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainBuilder{
    String url;
    BookBuilder book;
    ContentFetcher contentFetcher;
    ContentFetcher.ParseResult mainPage;

    String title;
    String author;

    public MainBuilder(String url) {
        this.url = url;
    }

    public String runInit() {
        contentFetcher = new ContentFetcher();
        try {
            mainPage = contentFetcher.fetchContentInitial(url);
        } catch (IOException e) {
            throw new RuntimeException("failed to get initial webpage" + e);
        }
        title = mainPage.parser.extractTitle(mainPage.document);
        author = mainPage.parser.extractAuthor(mainPage.document);
        List<Chapter> chapters = mainPage.parser.getChapterUrls(mainPage.document);
        return mainPage.parser.makeSaveAsFileNameWithoutExtension(title, false) + ".epub";
    }

    // and for the Handler that will update the UI from the main thread
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    // Create an interface to respond with the result after processing
    public interface OnProcessedListener {
        public void onProcessed();
    }
    public void runCollectChapters(List<Chapter> chapters, Context context) throws RuntimeException{
        try {

            book = new BookBuilder(title);
            if (author == null || author.isEmpty())
                author = title;
            var splitAuthor = author.split("[, ]");
            if (splitAuthor.length < 1)
                book.setAuthor("Undefined");
            else if (splitAuthor.length < 2)
                book.setAuthor(splitAuthor[0]);
            else if (splitAuthor.length < 3)
                book.setAuthor(splitAuthor[0], splitAuthor[1]);
            else
                book.setAuthor(splitAuthor[0], splitAuthor[splitAuthor.length - 1]);
            int i = 0;

            ArrayBlockingQueue<ContentFetcher.ParseResult> queue =
                    new ArrayBlockingQueue<>(chapters.size());

            for (Chapter chapter : chapters) {
                i++;

                ContentFetcher.ParseResult chapterPage;
                try {
                    chapterPage = contentFetcher.fetchContent(chapter.getSourceUrl());
                } catch (IOException e) {
                    throw new RuntimeException("failed to get chapter webpage " + chapter.getSourceUrl() + e);
                }
                mainPage.parser.removeUnusedElementsToReduceMemoryConsumption(chapterPage.document);
                Element chapterContent = mainPage.parser.findContent(chapterPage.document);
                mainPage.parser.removeUnwantedElementsFromContentElement(chapterContent);
                mainPage.parser.addTitleToContent(chapterPage.document, chapterContent);
                book.addChapter(chapter.getTitle(), toXHTML(chapterContent.html(), chapter.getTitle()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String toXHTML(String html, String title) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        addHeadElements(document, title);
        document.getElementsByTag("html").attr("xmlns","http://www.w3.org/1999/xhtml");
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        sb.append("<!DOCTYPE html>\n");
        return sb.append(document.html()).toString();
    }

    private void addHeadElements(Document doc, String title) {
        Element style = new Element("link", Util.XMLNS);
        doc.head().appendChild(style);
        style.attr("href", "epub.css");
        style.attr("type", "text/css");
        style.attr("rel", "stylesheet");
        Element titleElem = new Element("title", Util.XMLNS);
        doc.head().appendChild(titleElem);
        titleElem.text(title);
    }
    public void addStyleSheet(@NotNull InputStream resourceStream, String href) {
        book.addOtherResource(resourceStream, href);
    }
    public void build(String path) {
        book.build(path);
    }

    public void build(FileOutputStream stream) {
        book.build(stream);
    }

}
