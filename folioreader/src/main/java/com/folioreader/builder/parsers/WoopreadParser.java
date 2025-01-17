package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WoopreadParser extends Parser {

    public WoopreadParser() {
        super();
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst("div.post-title h1");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div.description-summary");
    }

    @Override
    public String extractAuthor(Document doc) {
        Element authorLabel = doc.selectFirst("div.author-content a");
        return authorLabel.text();
    }

    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.summary_image");
    }

    public List<Chapter> getChapterUrls(Document doc) {
        Elements menu = doc.select("ul.version-chap");
        return Util.hyperlinksToChapterList(menu.first());
    }

    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("h3");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("div.text-left");
    }
}
