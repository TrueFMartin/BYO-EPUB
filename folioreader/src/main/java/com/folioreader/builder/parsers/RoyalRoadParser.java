package com.folioreader.builder.parsers;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

public class RoyalRoadParser extends Parser {

    public RoyalRoadParser() {
        super();
    }

    public int clampSimultanousFetchSize() {
        return 1;
    }

    public List<String> getChapterUrls(Document dom) throws IOException {
        // Fetch new page to get all chapter links (using JSoup)
        Document tocHtml = Jsoup.connect(dom.baseUri()).get();
        Elements table = tocHtml.select("table#chapters");
        return Util.hyperlinksToChapterList(table);
    }

    public Element findContent(Document dom) {
        Elements contents = dom.select("div.portlet-body div.chapter-inner, .page-content-wrapper");
        return contents.first();
    }

    public void populateUI(Document dom) {
        // UI related methods are not directly translatable to Java
    }

    public void removeUnwantedElementsFromContentElement(Element content) {
        // Implementation to remove unwanted elements from content
        // Keep only the <div class="chapter-inner"> elements
        Elements children = content.children();
        children.forEach(child -> {
            if (!isWantedElement(child)) {
                child.remove();
            }
        });
        makeHiddenElementsVisible(content);
    }

    private boolean isWantedElement(Element element) {
        String tagName = element.tagName().toLowerCase();
        String className = element.className();
        return tagName.equals("h1") ||
                (tagName.equals("div") &&
                        (className.startsWith("chapter-inner") ||
                                className.contains("author-note-portlet") ||
                                className.contains("page-content")));
    }

    private void makeHiddenElementsVisible(Element content) {
        content.select("div").stream()
                .filter(e -> "none".equals(e.attr("style")))
                .forEach(e -> e.removeAttr("style"));
    }

    public String extractTitleImpl(Document dom) {
        Element titleElement = dom.select("div.fic-header div.col h1").first();
        return titleElement != null ? titleElement.text() : null;
    }

    public String extractAuthor(Document dom) {
        Element authorElement = dom.select("div.fic-header h4 span a").first();
        return authorElement != null ? authorElement.text().trim() : super.extractAuthor(dom);
    }

    public String extractSubject(Document dom) {
        Elements tags = dom.select("div.fiction-info span.tags .label");
        return tags.stream().map(Element::text).collect(Collectors.joining(", "));
    }

    public String extractDescription(Document dom) {
        Element descriptionElement = dom.select("div.fiction-info div.description").first();
        return descriptionElement != null ? descriptionElement.text().trim() : null;
    }

    public Element findChapterTitle(Document dom) {
        return dom.select("h1, h2").first();
    }

    public static void removeOlderChapterNavJunk(Element content) {
        // some older chapters have next chapter & previous chapter links separated by string "<-->"
        content.select("*").stream()
                .filter(n -> n.text().trim().equals("<-->"))
                .forEach(Element::remove);
    }

    public String findCoverImageUrl(Document dom) {
        Element coverImage = dom.select("img.thumbnail").first();
        return coverImage != null ? coverImage.attr("src") : null;
    }

    public void removeUnusedElementsToReduceMemoryConsumption(Document webPageDom) {
        super.removeUnusedElementsToReduceMemoryConsumption(webPageDom);
        removeImgTagsWithNoSrc(webPageDom);
        tagAuthorNotesBySelector(webPageDom, "div.author-note-portlet");
    }

    private void removeImgTagsWithNoSrc(Document webPageDom) {
        webPageDom.select("img").stream()
                .filter(i -> i.attr("src").isEmpty())
                .forEach(Element::remove);
    }

    private void tagAuthorNotesBySelector(Document webPageDom, String selector) {
        // Method to tag author notes, if needed
    }

    // Additional utility methods...

}

