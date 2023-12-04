package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class RubymaybetranslationsParser extends Parser {


    public RubymaybetranslationsParser() {
        super();
    }

    public RubymaybetranslationsParser(Void unused) {
        super();
    }

    public List<Chapter> getChapterUrls(Document doc) {
        Element links = doc.select("details a").first();
        if (links != null) {
            return Util.hyperlinksToChapterList(links);
        }
        return null;
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("div.entry-content");
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst(".nv-page-title h1");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public String extractAuthor(Document doc) {
        return null;
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("h1");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    // findParentNodeOfChapterLinkToRemoveAt method is omitted as it requires custom implementation

    public String findCoverImageUrl(Document doc) {
        // Cover image URL is not available
        return null;
    }

    @Override
    public void preprocessRawDom(Document webPageDom) {
        Util.removeChildElementsMatchingCss(webPageDom, "#comments");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select(".entry-content > p");
    }
}
