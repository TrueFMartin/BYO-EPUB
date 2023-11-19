package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.stream.Collectors;

public class WuxiacityParser extends Parser {

    public WuxiacityParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        if (!doc.baseUri().endsWith("/table-of-contents")) {
            // In Java, you need to handle fetching the table-of-contents page separately
            // and then pass the new Document to this method
        }
        Elements links = doc.select("ul.chapters a");
        return links.stream().map(WuxiacityParser::linkToChapter).collect(Collectors.toList());
    }

    private static Chapter linkToChapter(Element link) {
        return new Chapter(
                link.attr("href"),
                link.select(".chapter-name").text().replaceAll("\\n", " ")
        );
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("#chapter-content");
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst(".book-name");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst(".chapter-title p");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    @Override
    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.book-img");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div.book-synopsis");
    }
}
