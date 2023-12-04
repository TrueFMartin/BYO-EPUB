package com.folioreader.builder.parsers;


import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WuxiaworldParser extends Parser {

    static {
    }

    public WuxiaworldParser() {
        super();
    }

    public WuxiaworldParser(Void unused) {
        super();
    }


    @Override
    public List<Chapter> getChapterUrls(Document doc) {
        Elements chaptersElement = doc.select("div.content div.panel-group");
        List<Chapter> chapters;
        chapters = Util.hyperlinksToChapterList(chaptersElement.first());
        WuxiaworldParser.removeArcsWhenOnlyOne(chapters);
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
        long arcCount = chapters.stream().filter(c -> !c.getNewArc().isEmpty()).count();
        if (arcCount < 2) {
            chapters.forEach(chapter -> chapter.setNewArc("New Arc"));
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
    public String extractTitleImpl(Document doc) {
        return null;
    }

    @Override
    public String extractAuthor(Document doc) {
        return null;
    }

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

