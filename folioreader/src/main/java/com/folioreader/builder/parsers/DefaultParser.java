package com.folioreader.builder.parsers;

import com.folioreader.builder.Chapter;
import com.folioreader.builder.Parser;
import com.folioreader.builder.Util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;


public class DefaultParser extends Parser {

    String titleSelect;
    String authorSelect;
    String chapterSelect;
    String chapterTitleSelect;
    String bodySelect;
    String excludeSelect;
    public DefaultParser() {
        super();
    }

    public List<Chapter> getChapterUrls(Document dom) {
        return Util.hyperlinksToChapterList(dom.body());
    }

    public Element findContent(Document dom) {
        String hostName = Util.extractHostName(dom.baseUri());
        return dom.selectFirst("body");
    }

    public void populateUI(Document dom) {

    }

    public void removeUnwantedElementsFromContentElement(Element element) {
        Util.removeElements(element.select("script[src], iframe" + ", " + excludeSelect));
        Util.removeComments(element);
        Util.removeUnwantedWordpressElements(element);
        Util.removeMicrosoftWordCrapElements(element);
    }

    public String findChapterTitle(Document dom) {
        return dom.selectFirst(chapterTitleSelect).text();
    }

    @Override
    public String extractTitleImpl(Document doc) {
        return doc.selectFirst(titleSelect).text();
    }

    @Override
    public String extractAuthor(Document doc) {
        return doc.selectFirst(authorSelect).text();
    }

    @Override
    public Elements getInformationEpubItemChildNodes(Document dom) {
        return null;
    }

}

