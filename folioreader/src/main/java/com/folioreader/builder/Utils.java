package com.folioreader.builder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static void removeElements(Elements elements) {
        for (Element element : elements) {
            element.remove();
        }
    }

    public static void removeChildElementsMatchingCss(Element element, String cssQuery) {
        Elements elementsToRemove = element.select(cssQuery);
        removeElements(elementsToRemove);
    }

    public static String getFirstImgSrc(Document doc, String selector) {
        Element img = doc.select(selector + " img").first();
        return img != null ? img.attr("src") : null;
    }

    public static void setBaseTag(String url, Document doc) {
        Element baseTag = doc.select("base").first();
        if (baseTag != null) {
            baseTag.attr("href", url);
        } else {
            baseTag = doc.createElement("base");
            baseTag.attr("href", url);
            doc.head().appendChild(baseTag);
        }
    }

    public static void resolveLazyLoadedImages(Element content, String imgCss) {
        Elements images = content.select(imgCss);
        for (Element img : images) {
            String dataSrc = img.attr("data-src");
            if (dataSrc != null && !dataSrc.isEmpty()) {
                img.attr("src", dataSrc.trim());
            }
        }
    }

    public static String convertHtmlToXhtml(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    // Additional methods for DOM manipulation can be added here as needed.
}
