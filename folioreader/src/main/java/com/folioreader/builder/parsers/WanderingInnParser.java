package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;
import java.util.stream.Collectors;

public class WanderinginnParser extends WordpressBaseParser {

    public WanderinginnParser() {
        super();
    }

    public List<String> getChapterUrls(Document dom) {
        Elements chapterLinks = dom.select("#table-of-contents a:not(.book-title-num)");
        return chapterLinks.stream()
                .map(Element::absUrl)
                .collect(Collectors.toList());
    }

    public String extractTitleImpl() {
        return "The Wandering Inn"; // The title is statically defined
    }

    public String extractAuthor() {
        return "pirateaba"; // The author is statically defined
    }
}
