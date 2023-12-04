package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class SitesGoogleParser extends Parser {

    public SitesGoogleParser() {
        super();
    }

    public List<Chapter> getChapterUrls(Document doc) {
        Element menu = doc.selectFirst("ul[role='navigation']");
        if (menu == null) {
            menu = this.findContent(doc);
        }
        return Util.hyperlinksToChapterList(menu);
    }

    public Element findContent(Document doc) {
        return doc.selectFirst("div[role='main']");
    }

    @Override
    public String findChapterTitle(Document doc) {
        return null;
    }

    @Override
    public String extractTitleImpl(Document doc) {
        return null;
    }

    @Override
    public String extractAuthor(Document doc) {
        return null;
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document dom) {
        return null;
    }
}
