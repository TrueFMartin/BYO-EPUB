package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.List;

public class WanderinginnParser extends WordpressBaseParser {

    public WanderinginnParser() {
        super();
    }

    public WanderinginnParser(Void unused) {
        super();
    }

    public List<Chapter> getChapterUrls(Document dom) {
        Elements chapterLinks = dom.select("#table-of-contents a:not(.book-title-num)");
        return Util.hyperlinksToChapterList(chapterLinks.first());
    }

    public String extractTitleImpl() {
        return "The Wandering Inn"; // The title is statically defined
    }

    public String extractAuthor() {
        return "pirateaba"; // The author is statically defined
    }
}
