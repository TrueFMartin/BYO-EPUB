package com.folioreader.builder;


import com.folioreader.byobook.BookBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseParser {

    class ChapterContainer {
        String title;
        List<String> body;
        String url;

        public ChapterContainer(String title, String url) {
            this.title = title;
            this.body = new ArrayList<>();
            this.url = url;
        }
    }

    public void run(String url) {
        BookBuilder book = new BookBuilder("The Wandering Inn");
        book.setAuthor("Pirateaba", "Pirate");

        Elements tocElements = findTableOfContents();
        List<ChapterContainer> chapters = setBookURLs(tocElements);

        for (int i = 0; i < chapters.size(); i++) {
            ChapterContainer chapter = chapters.get(i);
            Document doc = getDocFromURL(chapter.url);
            Elements contentElements = doc.select(".entry-content");
            chapter.body = getChapterBody(contentElements);

            StringBuilder sectionBuilder = new StringBuilder();
            sectionBuilder.append("<h1>").append(chapter.title).append("</h1>");
            for (String paragraph : chapter.body) {
                sectionBuilder.append(paragraph);
            }
            book.addChapter(chapter.title, sectionBuilder.toString());

            // Progress display
            System.out.printf("\rProgress: %d/%d", i, chapters.size() - 1);
        }

        book.build("TheWanderingInn.epub");
    }

    private Document getDocFromURL(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Elements findTableOfContents() {
        return getDocFromURL("https://wanderinginn.com/table-of-contents/").select(".entry-content");
    }

    private List<ChapterContainer> setBookURLs(Elements elements) {
        List<ChapterContainer> chapters = new ArrayList<>();
        for (Element element : elements.select("a")) {
            String title = element.text();
            String url = element.attr("href");
            chapters.add(new ChapterContainer(title, url));
        }
        return chapters;
    }

    private List<String> getChapterBody(Elements elements) {
        List<String> body = new ArrayList<>();
        for (Element element : elements.select("p")) {
            body.add("<p>" + element.text() + "</p>\n");
        }
        return body;
    }
}
