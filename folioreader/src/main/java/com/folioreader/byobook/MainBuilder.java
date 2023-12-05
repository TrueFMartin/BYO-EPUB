package com.folioreader.byobook;

import android.util.Log;

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

public class MainBuilder{
    String url;
    BookBuilder book;

    public MainBuilder(String url) {
        this.url = url;
    }

    public String run() {
        var contentFetcher = new ContentFetcher();
        ContentFetcher.ParseResult mainPage;
        try {
            mainPage = contentFetcher.fetchContentInitial(url);
        } catch (IOException e) {
            throw new RuntimeException("failed to get initial webpage" + e);
        }
        String title = mainPage.parser.extractTitle(mainPage.document);
        String author = mainPage.parser.extractAuthor(mainPage.document);
        List<Chapter> chapters = mainPage.parser.getChapterUrls(mainPage.document);

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
            book.setAuthor(splitAuthor[0], splitAuthor[splitAuthor.length-1]);
        int i = 0;
        for (Chapter chapter: chapters) {
            i++;
            // FIXME remove after testing
            if (i > 2) {
                Log.e("MainBuilder", "Ending chapter load because greater than 2");
                break;
            }
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
        return mainPage.parser.makeSaveAsFileNameWithoutExtension(title, false) + ".epub";
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
    public void addStyleSheet(@NotNull InputStream resourceStream) {
        book.setStyleSheet(resourceStream);
    }
    public void build(String path) {
        book.build(path);
    }

    public void build(FileOutputStream stream) {
        book.build(stream);
    }

}
