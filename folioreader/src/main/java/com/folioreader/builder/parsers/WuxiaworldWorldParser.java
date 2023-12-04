package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WuxiaworldWorldParser extends Parser {



    public WuxiaworldWorldParser() {
        super();
    }

    public WuxiaworldWorldParser(Void unused) {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements menu = doc.select("div.manga_chapter_list");
        return Util.hyperlinksToChapterList(menu.first());
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("div.list_img");
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst("div.manga_name h1");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public String extractAuthor(Document doc) {
        Element authorLabel = doc.selectFirst("div.manga_des a");
        return authorLabel.text();
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("div.manga_view_name h1");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.manga_info_img");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div.manga_description");
    }
}
