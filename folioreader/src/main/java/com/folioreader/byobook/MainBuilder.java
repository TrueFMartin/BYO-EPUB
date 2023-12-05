package com.folioreader.byobook;

import android.util.Log;

import com.folioreader.builder.Chapter;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class MainBuilder{
    String url;
    public MainBuilder(String url) {
        this.url = url;
    }

    public void run() {
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

        BookBuilder book = new BookBuilder(title);
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
            book.addChapter(chapter.getTitle(), chapterContent.text());
        }
        String fileName = mainPage.parser.makeSaveAsFileNameWithoutExtension(title, false);
        book.build(fileName + ".epub");
    }
}
