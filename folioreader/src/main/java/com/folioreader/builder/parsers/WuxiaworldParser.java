package com.folioreader.builder.parsers;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.stream.Collectors;
import java.util.List;

public class WuxiaworldParser extends Parser {

    static {
        ParserFactory.register("wuxiaworld.com", WuxiaworldParser::new);
    }

    public WuxiaworldParser() {
        super();
    }

    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements chaptersElement = doc.select("div.content div.panel-group");
        List<Chapter> chapters;
        if (chaptersElement != null) {
            chapters = Util.hyperlinksToChapterList(chaptersElement,
                    WuxiaworldParser::isChapterHref, WuxiaworldParser::getChapterArc);
            WuxiaworldParser.removeArcsWhenOnlyOne(chapters);
        } else {
            chapters = doc.select("li.chapter-item a").stream()
                    .map(link -> Util.hyperLinkToChapter(link))
                    .collect(Collectors.toList());
        }
        return chapters;
    }

    private static boolean isChapterHref(Element link) {
        Element parent = link.parent();
        return "li".equals(parent.tagName()) && "chapter-item".equals(parent.className());
    }

    private static String getChapterArc(Element link) {
        Element panel = link.parents().select("div.panel.panel-default").first();
        Element arc = panel != null ? panel.selectFirst("span.title a") : null;
        return arc != null ? arc.text().trim() : null;
    }

    private static void removeArcsWhenOnlyOne(List<Chapter> chapters) {
        long arcCount = chapters.stream().filter(c -> c.getNewArc() != null).count();
        if (arcCount < 2) {
            chapters.forEach(c -> c.setNewArc(null));
        }
    }

    @Override
    public Element findContent(Document doc) {
        Elements candidates = doc.select("div.fr-view:not(.panel-body)");
        return WuxiaworldParser.elementWithMostParagraphs(candidates);
    }

    private static Element elementWithMostParagraphs(Elements elements) {
        if (elements.isEmpty()) {
            return null;
        }
        return elements.stream()
                .max(Comparator.comparingInt(e -> e.select("p").size()))
                .orElse(null);
    }

    private void cleanContent(Element content) {
        Util.removeChildElementsMatchingCss(content, "button, #spoiler_teaser");
        Elements toDelete = content.select("a").stream()
                .filter(a -> "Teaser".equals(a.text()))
                .collect(Collectors.toCollection(Elements::new));
        Util.removeElements(toDelete);
    }

    @Override
    public String findChapterTitle(Document doc) {
        Element chapterTitle = doc.selectFirst("div.caption h4");
        return chapterTitle != null ? chapterTitle.text() : null;
    }

    @Override
    public String findCoverImageUrl(Document doc) {
        return Util.getFirstImgSrc(doc, "div.novel-index");
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document doc) {
        Elements nodes = doc.select("div.media-novel-index div.media-body");
        Elements summary = doc.select("div.fr-view");
        nodes.addAll(summary);
        return nodes;
    }
}

