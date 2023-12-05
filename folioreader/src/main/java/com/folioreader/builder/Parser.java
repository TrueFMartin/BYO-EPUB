package com.folioreader.builder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

class ParserState {
    private Map<String, Document> webPages;
    private String chapterListUrl;

    public ParserState() {
        this.webPages = new HashMap<>();
        this.chapterListUrl = null;
    }

    public void setPagesToFetch(List<Document> urls) {
        Set<String> nextPrevChapters = new HashSet<>();
        this.webPages = new HashMap<>();

        for (int i = 0; i < urls.size(); ++i) {
            Document page = urls.get(i);
            if (i < urls.size() - 1) {
                nextPrevChapters.add(Util.normalizeUrlForCompare(urls.get(i + 1).baseUri()));
            }
            this.webPages.put(page.baseUri(), page);

            nextPrevChapters.clear();
            nextPrevChapters.add(Util.normalizeUrlForCompare(page.baseUri()));
        }
    }

    // Getters and setters for webPages and chapterListUrl
    public Map<String, Document> getDocuments() {
        return webPages;
    }

    public String getChapterListUrl() {
        return chapterListUrl;
    }

    public void setChapterListUrl(String chapterListUrl) {
        this.chapterListUrl = chapterListUrl;
    }
}

// The Document class is assumed to have methods like getSourceUrl() and setNextPrevChapters().
// The Util class is assumed to have a method normalizeUrlForCompare().

public abstract class Parser {
    public static String WEB_TO_EPUB_CLASS_NAME = "webToEpubContent";

    Util util = Util.getInstance();
    private ParserState state;
    private ImageCollector imageCollector;
    private UserPreferences userPreferences;
//    private UserPreferences userPreferences;

    public Parser(ImageCollector imageCollector) {
        this.state = new ParserState();
        this.imageCollector = imageCollector != null ? imageCollector : new ImageCollector();
//        this.userPreferences = null;
    }
    public Parser() {
        this.state = new ParserState();
    }

    public void copyState(Parser otherParser) {
        this.state = otherParser.state;
        this.imageCollector.copyState(otherParser.imageCollector);
        this.userPreferences = otherParser.userPreferences;
    }

    public void setPagesToFetch(List<Document> urls) {
        this.state.setPagesToFetch(urls);
    }

    public Map<String, Document> getPagesToFetch() {
        return this.state.getDocuments();
    }

    public void onUserPreferencesUpdate(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        this.imageCollector.onUserPreferencesUpdate(userPreferences);
    }
    public List<Chapter> getChapterUrls(Document doc) {
        return Util.hyperlinksToChapterList(doc.select("a").first());
    }
    public void removeUnwantedElementsFromContentElement(Element element) {
        Util.removeScriptableElements(element);
        Util.removeComments(element);
        Util.removeElements(element.select("noscript, input"));
        Util.removeUnwantedWordpressElements(element);
        Util.removeMicrosoftWordCrapElements(element);
        Util.removeShareLinkElements(element);
        Util.removeLeadingWhiteSpace(element);
    };


    // Additional methods to be implemented

    // Abstract methods that need to be overridden in derived classes
    public abstract Element findContent(Document doc);
    public abstract String findChapterTitle(Document doc);


    // Additional methods in Parser class
    public void addTitleToContent(Document webPage, Element content) {
        String title = findChapterTitle(webPage);
        if (title != null && !titleAlreadyPresent(title, content)) {
            Element titleElement = webPage.createElement("h1");
            titleElement.text(title.trim());
            content.prependChild(titleElement);
        }
    }

    private boolean titleAlreadyPresent(String title, Element content) {
        Element existingTitle = content.select("h1, h2, h3, h4, h5, h6").first();
        return existingTitle != null && title.trim().equals(existingTitle.text().trim());
    }

    // Additional abstract methods for subclasses to implement
    public abstract String extractTitleImpl(Document doc);
    public abstract String extractAuthor(Document doc);

    public String extractTitle(Document doc) {
        String title = extractTitleImpl(doc);
        if (title == null) {
            title = extractTitleDefault(doc);
        }
        return title != null ? title.trim() : null;
    }

    public static String extractTitleDefault(Document doc) {
        Element titleElement = doc.selectFirst("meta[property='og:title']");
        return titleElement != null ? titleElement.attr("content") : doc.title();
    }


    public String extractSubject(Document doc) {
        // Default implementation. Override in subclass if needed.
        return "";
    }

    public String extractDescription(Document doc) {
        // Default implementation. Override in subclass if needed.
        return "";
    }

    public void extractSeriesInfo(Document dom, EpubMetaInfo metaInfo) {
        // Default implementation. Derived classes will override.
    }

    public EpubMetaInfo getEpubMetaInfo(Document dom, boolean useFullTitle) {
        EpubMetaInfo metaInfo = new EpubMetaInfo();
//        metaInfo.setUuid(dom.baseUri());
//        metaInfo.setTitle(extractTitle(dom));
//        metaInfo.setAuthor(extractAuthor(dom).trim());
        return metaInfo;
    }

    public String makeSaveAsFileNameWithoutExtension(String title, boolean useFullTitle) {
        int maxFileNameLength = useFullTitle ? 512 : 20;
        String fileName = (title == null) ? "web" : Util.safeForFileName(title, maxFileNameLength);
        if (Util.isStringWhiteSpace(fileName)) {
            fileName = title; // Title is probably not in English, so use it as is.
        }
        return fileName;
    }


    public List<Chapter> singleChapterStory(String baseUrl, Document dom) {
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter(baseUrl, extractTitle(dom)));
        return chapters;
    }

    public String getBaseUrl(Document dom) {
        Element baseElement = dom.selectFirst("base");
        return baseElement != null ? baseElement.attr("href") : null;
    }


     public void fixupHyperlinksInEpubItems(List<EpubItem> epubItems) {
        Map<String, String> targets = sourceUrlToEpubItemUrl(epubItems);
        for (EpubItem item : epubItems) {
            for (Element link : item.getHyperlinks()) {
                if (isUnresolvedHyperlink(link)) {
                    if (!hyperlinkToEpubItemUrl(link, targets)) {
                        makeHyperlinkAbsolute(link);
                    }
                }
            }
        }
    }

    private Map<String, String> sourceUrlToEpubItemUrl(List<EpubItem> epubItems) {
        Map<String, String> targets = new HashMap<>();
        for (EpubItem item : epubItems) {
            String key = Util.normalizeUrlForCompare(item.sourceUrl);
            if (!targets.containsKey(key)) {
                targets.put(key, Util.makeRelative(item.getZipHref()));
            }
        }
        return targets;
    }

    private boolean isUnresolvedHyperlink(Element link) {
        String href = link.attr("href");
        return href != null && !href.startsWith("#") && !href.startsWith("../Text/");
    }

    private boolean hyperlinkToEpubItemUrl(Element link, Map<String, String> targets) {
        String key = Util.normalizeUrlForCompare(link.attr("href"));
        if (targets.containsKey(key)) {
            link.attr("href", targets.get(key) + link.attr("hash"));
            return true;
        }
        return false;
    }

    private void makeHyperlinkAbsolute(Element link) {
        link.attr("href", link.absUrl("href"));
    }


    public void tagAuthorNotes(List<Element> elements) {
        for (Element element : elements) {
            element.addClass("webToEpub-author-note");
        }
    }


    // Helper method for creating an empty document for content
    public static Document makeEmptyDocForContent(String baseUrl) {
        Document doc = Document.createShell(baseUrl);
        Element content = doc.createElement("div");
        content.addClass(Parser.WEB_TO_EPUB_CLASS_NAME);
        doc.body().appendChild(content);
        return doc;
    }

    public static Element findConstructedContent(Document doc) {
        return doc.selectFirst("div." + Parser.WEB_TO_EPUB_CLASS_NAME);
    }


    public void moveFootnotes(Document dom, Element content, List<Element> footnotes) {
        if (!footnotes.isEmpty()) {
            Element list = dom.createElement("ol");
            for (Element footnote : footnotes) {
                Element item = dom.createElement("li");
                footnote.removeAttr("style");
                item.appendChild(footnote);
                list.appendChild(item);
            }
            Element header = dom.createElement("h2");
            header.text("Footnotes");
            content.appendChild(header);
            content.appendChild(list);
        }
    }

    public List<Document> walkPagesOfChapter(String url, Function<Document, String> moreChapterTextUrl) throws IOException {
        List<Document> chapterPages = new ArrayList<>();
        Document dom = Jsoup.connect(url).get();
        chapterPages.add(dom);
        String nextUrl = moreChapterTextUrl.apply(dom);
        while (nextUrl != null) {
            dom = Jsoup.connect(nextUrl).get();
            chapterPages.add(dom);
            nextUrl = moreChapterTextUrl.apply(dom);
        }
        return chapterPages;
    }
    public void rateLimitDelay() throws InterruptedException {
        long delay = determineRateLimitDelay();
        Thread.sleep(delay);
    }

    private long determineRateLimitDelay() {
        // Logic to determine delay based on user preferences or other factors
        return userPreferences.delay;
    }


    // Methods to walk through ToC pages and fetch chapters
    public List<Chapter> walkTocPages(Document dom, Function<Document, List<Chapter>> chaptersFromDom,
                                      Function<Document, String> nextTocPageUrl) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        while (dom != null) {
            List<Chapter> partialList = chaptersFromDom.apply(dom);
            chapters.addAll(partialList);
            String url = nextTocPageUrl.apply(dom);
            if (url != null) {
                dom = Jsoup.connect(url).get();
            } else {
                break;
            }
        }
        return chapters;
    }


    public void preprocessRawDom(Document webPageDom) {
        // Default implementation; override in subclasses as needed.
    }

    public void removeUnusedElementsToReduceMemoryConsumption(Document webPageDom) {
        Util.removeElements(webPageDom.select("select, iframe"));
    }

    public Document fetchChapter(String url) throws IOException {
        // Fetches the chapter content; override in subclasses for specific behavior.
        return Jsoup.connect(url).get();
    }

    public void updateReadingList() {
        // Update reading list based on state and user preferences.
        // Implementation depends on your application's reading list management.
    }

    public void updateLoadState(Document webPage) {
        // Update load state of the web page.
        // Implementation depends on your application's UI and progress tracking.
    }

    public void fetchImagesUsedInDocument(Element content, Document webPage) throws IOException {
        // Implementation for fetching and handling images within the document.
        // This would typically involve imageCollector and other utility methods.
    }

    public List<EpubItem> webPagesToEpubItems(List<Document> webPages) {
        List<EpubItem> epubItems = new ArrayList<>();
        // Convert each web page to Epub items.
        // Implementation depends on EpubItem creation and handling.
        return epubItems;
    }

    public EpubItem makeInformationEpubItem(Document dom) {
        // Create an Epub item for information page.
        // Implementation depends on your EpubItem and specific info page requirements.
        return null;
    }


    public void onStartCollecting() {
        // Hook point, called when "Pack EPUB" is pressed. Override in derived classes if needed.
    }

    public void populateInfoDiv(Element infoDiv, Document dom) {
        Sanitize sanitize = new Sanitize();
        for (Element node : getInformationEpubItemChildNodes(dom)) {
            Element clone = node.clone();
            cleanInformationNode(clone);
            if (clone != null) {
                infoDiv.appendChild(sanitize.clean(clone));
            }
        }
        Util.removeChildElementsMatchingCss(infoDiv, "img"); // Remove images as they won't be collected
    }

    public void cleanInformationNode(Element node) {
        // Default implementation; override in derived classes as required.
    }

    public abstract Elements getInformationEpubItemChildNodes(Document dom);

    public void fetchContent() {
        // Implementation for fetching content.
        // This method would involve fetching web pages and handling them accordingly.
    }

    public void setUiToShowLoadingProgress(int length) {
        // UI handling method to show loading progress.
        // Implementation depends on your application's UI logic.
    }

    public List<EpubItem> fetchDocuments() throws IOException {
        // Fetches web pages and converts them to Epub items.
        // Implement the logic for fetching and handling web pages.
        return new ArrayList<>();
    }

    public void fetchImagesUsedInDocument(Element content, String sourceUrl) throws IOException {
        // Implementation for fetching images used in the document.
        // Involves imageCollector and other utility methods for image handling.
    }

    public List<Document> groupPagesToFetch(List<Document> webPages, int index) {
        int blockSize = Math.min(userPreferences.synchronousLimit, clampSimultanousFetchSize());
        return webPages.subList(index, Math.min(index + blockSize, webPages.size()));
    }

    public int clampSimultanousFetchSize() {
        // Adjust the simultaneous fetch size if needed, based on specific site capabilities
        return userPreferences.synchronousLimit;
    }


}

