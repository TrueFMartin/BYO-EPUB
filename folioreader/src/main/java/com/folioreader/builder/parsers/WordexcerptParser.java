package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WordexcerptParser extends Parser {


    public WordexcerptParser() {
        super();
    }

    public WordexcerptParser(Void unused) {
        super();
    }

    public List<Chapter> getChapterUrls(Document doc) {
        Elements menu = doc.select("div.listing-chapters_wrap");
        return Util.hyperlinksToChapterList(menu.first());

    }

    public Element findContent(Document doc) {
        return doc.selectFirst("div.reading-content div.text-left");
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst("div.post-title h1");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public String extractAuthor(Document doc) {
        Element authorLabel = doc.selectFirst("div.author-content a");
        return authorLabel.text();
    }

    @Override
    public void removeUnwantedElementsFromContentElement(Element element) {
        Elements ads = element.select("div.adsbyvli").parents().select("center");
        Util.removeElements(ads);
        // For B:IF elements, you need to handle them specifically in Java
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("ol.breadcrumb li.active");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.summary_image");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div.summary__content p");
    }
}
