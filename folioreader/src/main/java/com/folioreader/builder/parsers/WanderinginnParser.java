package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WanderinginnParser extends WordpressBaseParser {

    public WanderinginnParser() {
        super();
    }


    @Override
    public List<Chapter> getChapterUrls(Document dom) {
        Elements chapterLinks = dom.select("#table-of-contents a:not(.book-title-num)");
        var chapters = new ArrayList<Chapter>();
        chapters.ensureCapacity(chapters.size());
        chapterLinks.forEach(elem -> chapters.add(Util.hyperLinkToChapter(elem)));
        return chapters;
    }

    @Override
    public String extractTitleImpl(Document doc) {
        return "The Wandering Inn"; // The title is statically defined
    }

    @Override
    public String extractAuthor(Document doc) {
        return "pirateaba"; // The author is statically defined
    }
}
