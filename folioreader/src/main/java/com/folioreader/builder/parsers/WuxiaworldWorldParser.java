package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.stream.Collectors;

public class WuxiaworldWorldParser extends Parser {

    static {
        ParserFactory.register("wuxiaworld.world", WuxiaworldWorldParser::new);
    }

    public WuxiaworldWorldParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements menu = doc.select("div.manga_chapter_list");
        return Util.hyperlinksToChapterList(menu).stream()
                .map(Chapter::reverse)
                .collect(Collectors.toList());
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
        return authorLabel != null ? authorLabel.text() : super.extractAuthor(doc);
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("div.manga_view_name h1");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    @Override
    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.manga_info_img");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        return doc.select("div.manga_description");
    }
}
