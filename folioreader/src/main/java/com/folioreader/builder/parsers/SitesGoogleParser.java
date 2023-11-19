package com.folioreader.builder.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SitesGoogleParser extends Parser {

    public SitesGoogleParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Element menu = doc.selectFirst("ul[role='navigation']");
        if (menu == null) {
            menu = this.findContent(doc);
        }
        return Util.hyperlinksToChapterList(menu).stream().collect(Collectors.toList());
    }

    @Override
    public Element findContent(Document doc) {
        return doc.selectFirst("div[role='main']");
    }
}
