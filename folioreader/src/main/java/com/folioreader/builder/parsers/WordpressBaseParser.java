package com.folioreader.builder.parsers;

import com.folioreader.builder.Parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

public class WordpressBaseParser extends Parser {

    public WordpressBaseParser() {
        super();
    }

    public List<String> getChapterUrls(Document dom) {
        Element content = findContent(dom).clone();
        removeUnwantedElementsFromContentElement(content);
        return Util.hyperlinksToChapterList(content);
    }

    public static Element findContentElement(Document dom) {
        return dom.select("div.entry-content, div.post-content").first();
    }

    public Element findContent(Document dom) {
        return WordpressBaseParser.findContentElement(dom);
    }

    public Element findParentNodeOfChapterLinkToRemoveAt(Element link) {
        // "next" and "previous" chapter links may be inside <strong> then <p> tag
        Element toRemove = Util.moveIfParent(link, "strong");
        return Util.moveIfParent(toRemove, "p");
    }

    public static Element findChapterTitleElement(Document dom) {
        return dom.select(".entry-title, .page-title, header.post-title h1, .post-title, #chapter-heading").first();
    }

    public Element findChapterTitle(Document dom) {
        return WordpressBaseParser.findChapterTitleElement(dom);
    }

    @Override
    protected String extractTitleImpl(Document doc) {
        return null;
    }

    @Override
    protected String extractAuthor(Document doc) {
        return null;
    }

    @Override
    protected Elements getInformationEpubItemChildNodes(Document dom) {
        return null;
    }

}

