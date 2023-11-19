package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class RubymaybetranslationsParser extends Parser {

    static {
        ParserFactory.register("rubymaybetranslations.com", RubymaybetranslationsParser::new);
    }

    public RubymaybetranslationsParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements links = doc.select("details a");
        return links.stream().map(Util::hyperLinkToChapter).collect(Collectors.toList());
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
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("h1");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    // findParentNodeOfChapterLinkToRemoveAt method is omitted as it requires custom implementation

    @Override
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
