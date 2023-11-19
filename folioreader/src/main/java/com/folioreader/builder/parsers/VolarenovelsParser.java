package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class VolarenovelsParser extends Parser {

    static {
        ParserFactory.register("volarenovels.com", VolarenovelsParser::new);
    }

    public VolarenovelsParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements links = doc.select("ul.list-chapters a");
        return links.stream().map(Util::hyperLinkToChapter).collect(Collectors.toList());
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("div.panel-body div.fr-view");
    }

    @Override
    public String extractTitleImpl(Document doc) {
        Element titleElement = doc.selectFirst("h3.title");
        return titleElement != null ? titleElement.text() : null;
    }

    @Override
    public String extractDescription(Document doc) {
        Element descriptionElement = doc.selectFirst("div#Details");
        return descriptionElement != null ? descriptionElement.text().trim() : null;
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("div.panel-body h4");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    @Override
    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div#content-container");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div#Details");
    }
}
