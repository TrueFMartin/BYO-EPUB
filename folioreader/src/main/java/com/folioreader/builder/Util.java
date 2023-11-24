package com.folioreader.builder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {

    private static Util instance;
    private static final String[] HEADER_TAGS = {"h1", "h2", "h3", "h4", "h5", "h6"};

    private Util() {
    }

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public static void removeElements(Elements elements) {
        for (Element element : elements) {
            element.remove();
        }
    }

    public Document createEmptyXhtmlDoc() {
        Document doc = Jsoup.parse("<!DOCTYPE html><html></html>", "", org.jsoup.parser.Parser.xmlParser());
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        Element htmlNode = doc.selectFirst("html");

        Element head = htmlNode.appendElement("head");
        head.appendElement("title");
        // Assuming populateHead is appropriately converted to Java
        populateHead(doc, head);

        htmlNode.appendElement("body");
        return doc;
    }


    /**
     * Create an empty HTML document.
     *
     * @return A new HTML document.
     */
    public static Document createEmptyHtmlDoc() {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
        Element html = doc.createElement("html");
        doc.appendChild(html);
        Element head = doc.createElement("head");
        html.appendChild(head);
        populateHead(doc, head);
        Element body = doc.createElement("body");
        html.appendChild(body);
        return doc;
    }

    /**
     * Populate the head section of the document.
     *
     * @param doc  The document to populate.
     * @param head The head element of the document.
     */
    private static void populateHead(Document doc, Element head) {
        Element style = doc.createElement("link");
        head.appendChild(style);
        style.setAttribute("href", makeRelative(styleSheetFileName()));
        style.setAttribute("type", "text/css");
        style.setAttribute("rel", "stylesheet");
    }

    /**
     * Create an SVG image element.
     *
     * @param href                   The href for the image.
     * @param width                  The width of the image.
     * @param height                 The height of the image.
     * @param origin                 The origin of the image.
     * @param includeImageSourceUrl  Include the image source URL or not.
     * @return A div element containing the SVG image.
     */
    public static Element createSvgImageElement(String href, int width, int height, String origin, boolean includeImageSourceUrl) {
        Document doc = createEmptyXhtmlDoc();
        Element body = (Element) doc.select("body").get(0);
        Element div = doc.createElement("div");
        div.setAttribute("class", "svg_outer svg_inner");
        body.appendChild(div);
        Element svg = doc.createElement("svg");
        svg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        div.appendChild(svg);
        svg.setAttribute("height", "99%");
        svg.setAttribute("width", "100%");
        svg.setAttribute("version", "1.1");
        svg.setAttribute("preserveAspectRatio", "xMidYMid meet");
        svg.setAttribute("viewBox", "0 0 " + width + " " + height);
        Element newImage = doc.createElement("image");
        svg.appendChild(newImage);
        newImage.setAttribute("xlink:href", makeRelative(href));
        newImage.setAttribute("width", String.valueOf(width));
        newImage.setAttribute("height", String.valueOf(height));
        if (includeImageSourceUrl) {
            Element desc = doc.createElement("desc");
            svg.appendChild(desc);
            desc.appendChild(doc.createTextNode(origin));
        } else {
            svg.appendChild(createComment(doc, origin));
        }
        return div;
    }

    /**
     * Create a comment node.
     *
     * @param doc     The document to create the comment in.
     * @param content The content of the comment.
     * @return A new comment node.
     */
    public static Comment createComment(Document doc, String content) {
        return doc.createComment("  " + content.replace("--", "%2D%2D") + "  ");
    }

    // Assuming makeRelative and styleSheetFileName methods are already defined

    /**
     * Resolve a relative URL based on a base URL.
     *
     * @param baseUrl     The base URL.
     * @param relativeUrl The relative URL to resolve.
     * @return The resolved URL.
     */
    public static String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        try {
            URL base = new URL(baseUrl);
            return new URL(base, relativeUrl).toString();
        } catch (MalformedURLException e) {
            return null; // Or handle error appropriately
        }
    }

    /**
     * Extract the hostname from a URL.
     *
     * @param url The URL to extract the hostname from.
     * @return The hostname or an empty string if extraction fails.
     */
    public static String extractHostName(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            return ""; // Or handle error appropriately
        }
    }

    /**
     * Extract the filename from a hyperlink.
     *
     * @param hyperlink The hyperlink to extract the filename from.
     * @return The extracted filename or an empty string if extraction fails.
     */
    public static String extractFilename(String hyperlink) {
        try {
            String[] parts = new URL(hyperlink).getPath().split("/");
            return parts.length > 0 ? parts[parts.length - 1] : "";
        } catch (MalformedURLException e) {
            return ""; // Or handle error appropriately
        }
    }

    /**
     * Extract the filename from a URL.
     *
     * @param url The URL to extract the filename from.
     * @return The extracted filename or an empty string if extraction fails.
     */
    public static String extractFilenameFromUrl(String url) {
        try {
            String[] parts = new URL(url).getPath().split("/");
            return parts.length > 0 ? parts[parts.length - 1] : "";
        } catch (MalformedURLException e) {
            return ""; // Or handle error appropriately
        }
    }

    /**
     * Get a parameter value from a URL.
     *
     * @param url       The URL containing the parameter.
     * @param paramName The name of the parameter to retrieve.
     * @return The value of the parameter or null if not found.
     */
    public static String getParamFromUrl(String url, String paramName) {
        try {
            URL u = new URL(url);
            String query = u.getQuery();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (pair.substring(0, idx).equals(paramName)) {
                    return URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.toString());
                }
            }
        } catch (Exception e) {
            return null; // Or handle error appropriately
        }
        return null;
    }

    /**
     * Set the base tag of a DOM to a specified URL.
     *
     * @param url The URL to set as the base.
     * @param dom The DOM to modify.
     */
    public static void setBaseTag(String url, Document dom) {
        Elements tags = dom.select("base");
        if (tags.size() > 0) {
            ((Element) tags.get(0)).setAttribute("href", url);
        } else {
            Element baseTag = dom.createElement("base");
            baseTag.setAttribute("href", url);
            dom.select("head").get(0).appendChild(baseTag);
        }
    }

    /**
     * Decode Cloudflare protected emails.
     *
     * @param content The element containing encoded emails.
     */
    public static void decodeCloudflareProtectedEmails(Element content) {
        Elements links = content.querySelectorAll(".__cf_email__");
        for (int i = 0; i < links.size(); i++) {
            replaceCloudflareProtectedLink((Element) links.get(i));
        }
        links = content.querySelectorAll("a[href*='/cdn-cgi/l/email-protection']");
        for (int i = 0; i < links.size(); i++) {
            replaceCloudflareProtectedLink((Element) links.get(i));
        }
    }

    /**
     * Replace a Cloudflare protected link with a decoded email address.
     *
     * @param link The link element to replace.
     */
    private static void replaceCloudflareProtectedLink(Element link) {
        String cyptedEmail = link.attr("data-cfemail");
        if (cyptedEmail == null) {
            cyptedEmail = link.attr("href");
            if (cyptedEmail != null && !cyptedEmail.isEmpty()) {
                cyptedEmail = cyptedEmail.substring(1);
            }
        }
        if (cyptedEmail != null) {
            String decryptedEmail = decodeEmail(cyptedEmail);
            Text textNode = link.getOwnerDocument().createTextNode(decryptedEmail);
            link.parent().insertBefore(textNode, link);
            link.parent().removeChild(link);
        }
    }

    /**
     * Decode a Cloudflare encoded email.
     *
     * @param encodedString The encoded email string.
     * @return The decoded email address.
     */
    private static String decodeEmail(String encodedString) {
        int key = Integer.parseInt(encodedString.substring(0, 2), 16);
        StringBuilder email = new StringBuilder();
        for (int index = 2; index < encodedString.length(); index += 2) {
            email.append((char) (Integer.parseInt(encodedString.substring(index, index + 2), 16) ^ key));
        }
        return email.toString();
    }

    /**
     * Delete all nodes in the supplied Elements.
     *
     * @param elements The Elements of elements to remove.
     */
    public static void removeElements(Elements elements) {
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).parent().removeChild(elements.get(i));
        }
    }

    /**
     * Remove child elements matching a CSS selector.
     *
     * @param element The parent element.
     * @param css     The CSS selector.
     */
    public static void removeChildElementsMatchingCss(Element element, String css) {
        if (element != null) {
            Elements elementsToRemove = element.querySelectorAll(css);
            removeElements(elementsToRemove);
        }
    }

    /**
     * Remove comments from a root element.
     *
     * @param root The root element to start removing comments from.
     */
    public static void removeComments(Node root) {
        NodeFilter filter = (node) -> node.getNodeType() == Node.COMMENT_NODE ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
        NodeIterator iterator = ((Document) root).createNodeIterator(root, NodeFilter.SHOW_COMMENT, filter, false);
        Node node;
        while ((node = iterator.nextNode()) != null) {
            node.parent().removeChild(node);
        }
    }


    /**
     * Remove trailing white spaces from an element.
     *
     * @param element The element to remove trailing white spaces from.
     */
    public static void removeTrailingWhiteSpace(Node element) {
        Elements children = element.getChildNodes();
        for (int i = children.size() - 1; i >= 0; i--) {
            Node child = children.get(i);
            if (isElementWhiteSpace(child)) {
                element.removeChild(child);
            } else {
                break;
            }
        }
    }

    /**
     * Remove leading white spaces from an element.
     *
     * @param element The element to remove leading white spaces from.
     */
    public static void removeLeadingWhiteSpace(Node element) {
        Elements children = element.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (isElementWhiteSpace(child)) {
                element.removeChild(child);
            } else {
                break;
            }
        }
    }

    /**
     * Remove elements with scriptable content from an element.
     *
     * @param element The element to remove scriptable elements from.
     */
    public static void removeScriptableElements(Element element) {
        removeChildElementsMatchingCss(element, "script, iframe");
        removeEventHandlers(element);
    }

    /**
     * Remove Microsoft Word specific elements from an element.
     *
     * @param element The element to clean.
     */
    public static void removeMicrosoftWordCrapElements(Element element) {
        Elements nodes = element.select("O:P");
        for (int i = 0; i < nodes.size(); i++) {
            flattenNode((Element) nodes.get(i));
        }
    }

    /**
     * Flatten a node by moving its children to its parent and then removing the node.
     *
     * @param node The node to flatten.
     */
    private static void flattenNode(Node node) {
        Node parent = node.parent();
        while (node.hasChildNodes()) {
            parent.insertBefore(node.getFirstChild(), node);
        }
        parent.removeChild(node);
    }

    /**
     * Remove all event handlers from an element.
     *
     * @param contentElement The element to remove event handlers from.
     */
    private static void removeEventHandlers(Element contentElement) {
        Document document = contentElement.getOwnerDocument();
        NodeIterator iterator = document.createNodeIterator(contentElement, NodeFilter.SHOW_ELEMENT, null, false);
        Node node;
        while ((node = iterator.nextNode()) != null) {
            if (node instanceof Element) {
                ((Element) node).removeAttribute("onclick");
            }
        }
    }

    /**
     * Remove height and width styles from an element and its parents.
     *
     * @param element The element to remove height and width styles from.
     */
    public static void removeHeightAndWidthStyleFromParents(Element element) {
        Node parent = element.parent();
        while (parent != null && !"body".equalsIgnoreCase(parent.nodeName())) {
            removeHeightAndWidthStyle((Element) parent);
            parent = parent.parent();
        }
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
            if (!dataSrc.isEmpty()) {
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



    public static void removeComments(Element root) {
        root.childNodes().removeIf(node -> node.nodeName().equals("#comment"));
    }


    /**
     * Remove empty div elements that only contain white spaces.
     *
     * @param element The element to check for empty divs.
     */
    public static void removeEmptyDivElements(Element element) {
        Elements emptyDivs = element.select("div").stream()
                .filter(Util::isElementWhiteSpace)
                .collect(Collectors.toCollection(Elements::new));
        removeElements(emptyDivs);
    }

    public static void removeTrailingWhiteSpace(Element element) {
        while (!element.childNodes().isEmpty() && isElementWhiteSpace(element.childNode(element.childNodeSize() - 1))) {
            element.childNode(element.childNodeSize() - 1).remove();
        }
    }

    public static void removeLeadingWhiteSpace(Element element) {
        while (!element.childNodes().isEmpty() && isElementWhiteSpace(element.childNode(0))) {
            element.childNode(0).remove();
        }
    }

    public static void removeScriptableElements(Element element) {
        removeChildElementsMatchingCss(element, "script, iframe");
        removeEventHandlers(element);
    }

    public static void removeMicrosoftWordCrapElements(Element element) {
        Elements wordElements = element.select("O\\:P");
        for (Element wordElement : wordElements) {
            flattenNode(wordElement);
        }
    }

    public static void flattenNode(Element node) {
        while (!node.childNodes().isEmpty()) {
            node.before(node.childNode(0));
        }
        node.remove();
    }

    public static void removeEventHandlers(Element element) {
        Elements elementsWithEvents = element.select("*[onclick]");
        for (Element el : elementsWithEvents) {
            el.removeAttr("onclick");
        }
    }

    public static void removeHeightAndWidthStyleFromParents(Element element) {
        Element parent = element.parent();
        while (parent != null && !parent.tagName().equalsIgnoreCase("body")) {
            removeHeightAndWidthStyle(parent);
            parent = parent.parent();
        }
    }

    public static void removeHeightAndWidthStyle(Element element) {
        element.removeAttr("style");
        element.removeAttr("width");
        element.removeAttr("height");
    }

    public static void removeUnwantedWordpressElements(Element element) {
        String cssQuery = "div.sharedaddy, div.wpcnt, ul.post-categories, div.mistape_caption, "
                + "div.wpulike, div.wp-next-post-navi, .ezoic-adpicker-ad, .ezoic-ad, "
                + "ins.adsbygoogle";
        removeChildElementsMatchingCss(element, cssQuery);
    }

    public static void removeShareLinkElements(Element element) {
        removeChildElementsMatchingCss(element, "div.sharepost");
    }

    /**
     * Remove a specific style value from an element and its descendants.
     *
     * @param element   The root element.
     * @param styleName The name of the style to remove.
     * @param value     The value of the style to remove.
     */
    public static void removeStyleValue(Element element, String styleName, String value) {
        if (value != null) {
            element.traverse((node, x) -> {
                if (node instanceof Element) {
                    Element el = (Element) node;
                    String styleValue = el.attr("style");
                    if (styleValue.contains(styleName + ": " + value)) {
                        el.removeAttr("style");
                    }
                }
            });
        }
    }
    /**
     * Convert <pre> tags to <p> tags with specified split character.
     *
     * @param dom      The document containing the element.
     * @param element  The element to convert.
     * @param splitOn  The character to split the text on.
     */
    public static void convertPreTagToPTags(Document dom, Element element, String splitOn) {
        String[] lines = element.getTextContent().split(splitOn != null ? splitOn : "\n");
        element.text("");
        for (String line : lines) {
            Element p = dom.createElement("p");
            p.text(line);
            element.appendChild(p);
        }
    }

    /**
     * Prepare an element for conversion to XHTML.
     *
     * @param element The element to prepare.
     */
    public static void prepForConvertToXhtml(Element element) {
        replaceCenterTags(element);
        replaceUnderscoreTags(element);
        replaceSTags(element);
    }

    /**
     * Replace <center> tags with <p> tags having center-aligned text.
     *
     * @param element The element containing <center> tags.
     */
    public static void replaceCenterTags(Element element) {
        Elements centers = element.select("center");
        for (int i = 0; i < centers.size(); i++) {
            Element center = (Element) centers.get(i);
            Element replacement = center.getOwnerDocument().createElement("p");
            replacement.setAttribute("style", "text-align: center;");
            convertElement(center, replacement);
        }
    }

    /**
     * Replace <u> tags with <span> tags having underline text-decoration.
     *
     * @param element The element containing <u> tags.
     */
    public static void replaceUnderscoreTags(Element element) {
        Elements underscores = element.select("u");
        for (int i = 0; i < underscores.size(); i++) {
            Element underscore = (Element) underscores.get(i);
            Element replacement = underscore.getOwnerDocument().createElement("span");
            replacement.setAttribute("style", "text-decoration: underline;");
            convertElement(underscore, replacement);
        }
    }

    /**
     * Replace <s> tags with <span> tags having line-through text-decoration.
     *
     * @param element The element containing <s> tags.
     */
    public static void replaceSTags(Element element) {
        Elements strikethroughs = element.select("s");
        for (int i = 0; i < strikethroughs.size(); i++) {
            Element strikethrough = (Element) strikethroughs.get(i);
            Element replacement = strikethrough.getOwnerDocument().createElement("span");
            replacement.setAttribute("style", "text-decoration: line-through;");
            convertElement(strikethrough, replacement);
        }
    }

    /**
     * Convert an element to another type by moving its children and copying its attributes.
     *
     * @param element     The element to convert.
     * @param replacement The new element to replace the old one.
     */
    public static void convertElement(Element element, Element replacement) {
        Node parent = element.parent();
        parent.insertBefore(replacement, element);
        moveChildElements(element, replacement);
        copyAttributes(element, replacement);
        parent.removeChild(element);
    }

    /**
     * Move child elements from one element to another.
     *
     * @param from The element from which to move children.
     * @param to   The element to which to move children.
     */
    public static void moveChildElements(Element from, Element to) {
        while (from.hasChildNodes()) {
            to.appendChild(from.getFirstChild());
        }
    }

    /**
     * Copy attributes from one element to another.
     *
     * @param from The source element.
     * @param to   The destination element.
     */
    public static void copyAttributes(Element from, Element to) {
        NamedNodeMap attributes = from.attrs();
        for (int i = 0; i < attributes.size(); i++) {
            Node attr = attributes.get(i);
            to.setAttribute(attr.nodeName(), attr.getNodeValue());
        }
    }

    /**
     * Fix delay-loaded images by setting the 'src' attribute.
     *
     * @param element    The element containing images.
     * @param delayAttrib The attribute name containing the actual image URL.
     */
    public static void fixDelayLoadedImages(Element element, String delayAttrib) {
        Elements images = element.select("img");
        for (int i = 0; i < images.size(); i++) {
            Element img = (Element) images.get(i);
            String url = img.attr(delayAttrib);
            if (url != null && !url.isEmpty()) {
                img.setAttribute("src", url);
            }
        }
    }

    /**
     * Fix nested block tags within inline tags.
     *
     * @param contentElement The element to check for nested tags.
     */
    public static void fixBlockTagsNestedInInlineTags(Element contentElement) {
        NodeIterator iterator = contentElement.getOwnerDocument().createNodeIterator(
                contentElement, NodeFilter.SHOW_ELEMENT, null, false
        );
        Node element;
        while ((element = iterator.nextNode()) != null) {
            if (isInlineElement(element) && isBlockElementInside(element)) {
                moveElementsOutsideTag(element);
            }
        }
    }

    /**
     * Check if a block element is inside an inline element.
     *
     * @param inlineElement The inline element to check.
     * @return True if a block element is inside, false otherwise.
     */
    private static boolean isBlockElementInside(Node inlineElement) {
        NodeIterator iterator = inlineElement.getOwnerDocument().createNodeIterator(
                inlineElement, NodeFilter.SHOW_ELEMENT, null, false
        );
        Node element;
        while ((element = iterator.nextNode()) != null) {
            if (isBlockElement(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move elements outside their current tag.
     *
     * @param inlineElement The element whose children to move outside.
     */
    public static void moveElementsOutsideTag(Node inlineElement) {
        Node parent = inlineElement.parent();
        while (inlineElement.hasChildNodes()) {
            parent.insertBefore(inlineElement.getFirstChild(), inlineElement);
        }
    }


    /**
     * Check if a node is an inline element.
     *
     * @param node The node to check.
     * @return True if the node is an inline element, false otherwise.
     */
    public static boolean isInlineElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE &&
                Arrays.asList(INLINE_ELEMENTS).contains(node.nodeName().toLowerCase());
    }

    /**
     * Check if a node is a block element.
     *
     * @param node The node to check.
     * @return True if the node is a block element, false otherwise.
     */
    public static boolean isBlockElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE &&
                Arrays.asList(BLOCK_ELEMENTS).contains(node.nodeName().toLowerCase());
    }

    /**
     * Get the first image source URL within a specified selector.
     *
     * @param dom      The document to search within.
     * @param selector The CSS selector to use.
     * @return The source URL of the first image found, or null if none found.
     */
    public static String getFirstImgSrc(Document dom, String selector) {
        Element img = (Element) dom.querySelector(selector + " img");
        return img != null ? img.attr("src") : null;
    }

    /**
     * Extract the hash part from a URI.
     *
     * @param uri The URI to extract the hash from.
     * @return The extracted hash or null if not found.
     */
    public static String extractHashFromUri(String uri) {
        int index = uri.indexOf("#");
        return index != -1 ? uri.substring(index + 1) : null;
    }

    /**
     * Resolve lazy-loaded images by setting their 'src' attribute.
     *
     * @param content The content element containing images.
     * @param imgCss  The CSS selector for images.
     */
    public static void resolveLazyLoadedImages(Element content, String imgCss) {
        Elements imgs = content.querySelectorAll(imgCss);
        for (int i = 0; i < imgs.size(); i++) {
            Element img = (Element) imgs.get(i);
            String dataSrc = img.attr("data-src");
            if (dataSrc != null && !dataSrc.trim().isEmpty()) {
                img.setAttribute("src", dataSrc.trim());
            }
        }
    }

    /**
     * Convert hyperlinks to relative within a base URI.
     *
     * @param baseUri  The base URI for comparison.
     * @param content  The element containing hyperlinks.
     */
    public static void makeHyperlinksRelative(String baseUri, Element content) {
        Elements links = content.select("a");
        for (int i = 0; i < links.size(); i++) {
            Element link = (Element) links.get(i);
            if (isLocalHyperlink(baseUri, link)) {
                link.setAttribute("href", "#" + extractHashFromUri(link.attr("href")));
            }
        }
    }

    /**
     * Check if a hyperlink is local to a base URI.
     *
     * @param baseUri The base URI for comparison.
     * @param link    The hyperlink element.
     * @return True if the hyperlink is local, false otherwise.
     */
    private static boolean isLocalHyperlink(String baseUri, Element link) {
        String href = link.attr("href");
        return href.startsWith(baseUri) && href.contains("#");
    }

    /**
     * Find the most frequently used style properties in an element.
     *
     * @param element         The element to analyze.
     * @param styleProperties The style properties to check.
     * @return An array of the most frequently used values for each property.
     */
    public static String[] findPrimaryStyleSettings(Element element, String[] styleProperties) {
        // Implementation details would require a more complex analysis and are omitted for brevity.
        return new String[styleProperties.length]; // Placeholder implementation
    }


    /**
     * Set style properties to default values for an element and its descendants.
     *
     * @param element The element to reset style properties.
     */
    public static void setStyleToDefault(Element element) {
        String[] styleProperties = {"color", "fontSize"};
        String[] primary = findPrimaryStyleSettings(element, styleProperties);
        for (int i = 0; i < styleProperties.length; i++) {
            removeStyleValue(element, styleProperties[i], primary[i]);
        }
    }


    public static void setStyleToDefault(Element element) {
        String[] styleProperties = {"color", "fontSize"};
        String[] primary = findPrimaryStyleSettings(element, styleProperties);
        for (int i = 0; i < styleProperties.length; ++i) {
            removeStyleValue(element, styleProperties[i], primary[i]);
        }
    }

    public static void removeUnusedHeadingLevels(Element contentElement) {
        for (String tag : HEADER_TAGS) {
            Elements headings = contentElement.select(tag);
            if (!headings.isEmpty()) {
                headings.tagName(tag);
            }
        }
    }

    public static Node wrapRawTextNode(Node node) {
        if (node instanceof TextNode && !isStringWhiteSpace(((TextNode) node).text())) {
            Element wrapper = new Element("p");
            wrapper.appendChild(new TextNode(((TextNode) node).text()));
            return wrapper;
        } else {
            return node;
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }



    /**
     * Generate a list of chapters from hyperlinks.
     *
     * @param contentElement        The element containing the hyperlinks.
     * @param isChapterPredicate    A predicate to determine if a link is a chapter link.
     * @param getChapterArc         A function to get the chapter arc for a link.
     * @return A list of chapters.
     */
    public static List<Chapter> hyperlinksToChapterList(Element contentElement,
                                                        Predicate<Element> isChapterPredicate,
                                                        Function<Element, String> getChapterArc) {
        // Implementation details are omitted for brevity and would require custom classes and more complex logic.
        return new ArrayList<>(); // Placeholder implementation
    }

    /**
     * Remove the trailing slash from a URL.
     *
     * @param url The URL to process.
     * @return The URL without a trailing slash.
     */
    public static String removeTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Remove the anchor part from a URL.
     *
     * @param url The URL to process.
     * @return The URL without the anchor part.
     */
    public static String removeAnchor(String url) {
        int index = url.indexOf("#");
        return index >= 0 ? url.substring(0, index) : url;
    }

    /**
     * Normalize a URL for comparison by removing the trailing slash and anchor.
     *
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    public static String normalizeUrlForCompare(String url) {
        return removeTrailingSlash(removeAnchor(url));
    }

    /**
     * Convert a hyperlink to a chapter object.
     *
     * @param link   The hyperlink element.
     * @param newArc The new arc for the chapter.
     * @return A Chapter object representing the hyperlink.
     */
    public static Chapter hyperLinkToChapter(Element link, String newArc) {
        // Placeholder for Chapter class, which would need to be defined elsewhere
        return new Chapter(link.attr("href"), link.getTextContent().trim(), newArc);
    }

    /**
     * Create a comment node with escaped content.
     *
     * @param doc     The document to create the comment in.
     * @param content The content of the comment.
     * @return A new comment node with escaped content.
     */
    public static Comment createComment(Document doc, String content) {
        // Ensure the comment content does not contain double hyphens
        String escapedContent = content.replace("--", "%2D%2D");
        return doc.createComment("  " + escapedContent + "  ");
    }

    /**
     * Add an XML declaration to the start of a document.
     *
     * @param dom The DOM to add the declaration to.
     */
    public static void addXmlDeclarationToStart(Document dom) {
        // This method is a placeholder as Java's DOM API does not support adding XML declarations in this manner.
    }

    /**
     * Add an XHTML DOCTYPE to the start of a document.
     *
     * @param dom The DOM to add the DOCTYPE to.
     */
    public static void addXhtmlDocTypeToStart(Document dom) {
        DocumentType docType = dom.getImplementation().createDocumentType(
                "html",
                "-//W3C//DTD XHTML 1.1//EN",
                "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
        );
        dom.insertBefore(docType, dom.getDocumentElement());
    }

    /**
     * Check if a string is white space.
     *
     * @param s The string to check.
     * @return True if the string is only white space, false otherwise.
     */
    public static boolean isStringWhiteSpace(String s) {
        return s != null && s.trim().isEmpty();
    }


    /**
     * Check if a string is a valid URL.
     *
     * @param string The string to check.
     * @return True if the string is a valid URL, false otherwise.
     */
    public static boolean isUrl(String string) {
        try {
            URL url = new URL(string);
            return url.getProtocol().startsWith("http:") || url.getProtocol().startsWith("https:");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Convert an XML document to a string with an XML declaration.
     *
     * @param dom The XML document to convert.
     * @return A string representation of the XML document.
     */
    public static String xmlToString(Document dom) {
        addXmlDeclarationToStart(dom);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(dom), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            return null; // Or handle the exception as appropriate
        }
    }

    /**
     * Iterate over elements in a root node with a specified filter.
     *
     * @param root  The root node to start the iteration.
     * @param filter The filter to apply to the elements.
     * @return A list of elements that pass the filter.
     */
    public static List<Node> iterateElements(Node root, NodeFilter filter) {
        List<Node> elements = new ArrayList<>();
        NodeIterator iterator = root.getOwnerDocument().createNodeIterator(root, NodeFilter.SHOW_ELEMENT, filter, false);
        Node node;
        while ((node = iterator.nextNode()) != null) {
            elements.add(node);
        }
        return elements;
    }


    /**
     * Get the first element by tag name with an optional filter.
     *
     * @param dom     The document to search.
     * @param tagName The tag name to search for.
     * @param filter  An optional filter for the element.
     * @return The first element that matches the tag name and filter, or null if none found.
     */
    public static Element getElement(Document dom, String tagName, Predicate<Element> filter) {
        List<Element> elements = getElements(dom, tagName, filter);
        return elements.isEmpty() ? null : elements.get(0);
    }


    public static boolean isElementWhiteSpace(Node element) {
        if (element instanceof TextNode) {
            return isStringWhiteSpace(((TextNode) element).text());
        } else if (element instanceof Comment) {
            return true;
        } else if (element instanceof Element) {
            Element el = (Element) element;
            if (el.tagName().equals("img") || el.tagName().equals("image")) {
                return false;
            }
            if (el.select("img, image").size() > 0) {
                return false;
            }
            return isStringWhiteSpace(el.text());
        }
        return true;
    }

    public static boolean isHeaderTag(Node node) {
        if (node instanceof Element) {
            String tag = ((Element) node).tagName().toLowerCase();
            for (String headerTag : HEADER_TAGS) {
                if (tag.equals(headerTag)) {
                    return true;
                }
            }
        }
        return false;
    }



    public static String xmlToString(Document dom) {
        dom.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return dom.toString();
    }

    public static String zeroPad(int num, int length) {
        return String.format("%0" + length + "d", num);
    }

    /**
     * Convert an XML document to a string with an XML declaration.
     *
     * @param dom The XML document to convert.
     * @return A string representation of the XML document.
     */
    public static String xmlToString(Document dom) {
        addXmlDeclarationToStart(dom);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(dom), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            return null; // Or handle the exception as appropriate
        }
    }

    /**
     * Pad a number with zeros to a total of four digits.
     *
     * @param num The number to pad.
     * @return The zero-padded string.
     */
    public static String zeroPad(int num) {
        return String.format("%04d", num);
    }

    /**
     * Iterate over elements in a root node with a specified filter.
     *
     * @param root  The root node to start the iteration.
     * @param filter The filter to apply to the elements.
     * @return A list of elements that pass the filter.
     */
    public static List<Node> iterateElements(Node root, NodeFilter filter) {
        List<Node> elements = new ArrayList<>();
        NodeIterator iterator = root.getOwnerDocument().createNodeIterator(root, NodeFilter.SHOW_ELEMENT, filter, false);
        Node node;
        while ((node = iterator.nextNode()) != null) {
            elements.add(node);
        }
        return elements;
    }

    /**
     * Get elements by tag name with an optional filter.
     *
     * @param dom     The document to search.
     * @param tagName The tag name to search for.
     * @param filter  An optional filter for the elements.
     * @return A list of elements that match the tag name and filter.
     */
    public static List<Element> getElements(Document dom, String tagName, Predicate<Element> filter) {
        Elements nodes = dom.getElementsByTag(tagName);
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            Element element = (Element) nodes.get(i);
            if (filter == null || filter.test(element)) {
                elements.add(element);
            }
        }
        return elements;
    }

    /**
     * Get the first element by tag name with an optional filter.
     *
     * @param dom     The document to search.
     * @param tagName The tag name to search for.
     * @param filter  An optional filter for the element.
     * @return The first element that matches the tag name and filter, or null if none found.
     */
    public static Element getElement(Document dom, String tagName, Predicate<Element> filter) {
        List<Element> elements = getElements(dom, tagName, filter);
        return elements.isEmpty() ? null : elements.get(0);
    }
    /**
     * Move a node to a new parent if the current parent matches a specific tag.
     *
     * @param element    The element to move.
     * @param parentTag  The tag name of the parent to match.
     * @return The moved element, or the original element if the parent did not match.
     */
    public static Node moveIfParent(Node element, String parentTag) {
        Node parent = element.parent();
        if (parent.nodeName().equalsIgnoreCase(parentTag) && parent.().length() < 200) {
            parent.parent().insertBefore(element, parent);
            parent.parent().removeChild(parent);
            return element;
        }
        return element;
    }

    /**
     * Make a string safe for use as a file name, with a maximum length.
     *
     * @param title     The title to convert to a file name.
     * @param maxLength The maximum length of the file name.
     * @return A safe file name derived from the title.
     */
    public static String safeForFileName(String title, int maxLength) {
        if (title != null) {
            String safeTitle = title.replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9_-]", "");
            if (safeTitle.length() > maxLength) {
                int splitLength = (maxLength - 3) / 2;
                return safeTitle.substring(0, splitLength) + "..." + safeTitle.substring(safeTitle.length() - splitLength);
            }
            return safeTitle;
        }
        return "";
    }

    /**
     * Create a file name for storage.
     *
     * @param subdirectory The subdirectory for the file.
     * @param index        The index for the file.
     * @param title        The title of the file.
     * @param extension    The file extension.
     * @return A file name combining the subdirectory, index, title, and extension.
     */
    public static String makeStorageFileName(String subdirectory, int index, String title, String extension) {
        String safeTitle = title != null && !title.isEmpty() ? "_" + safeForFileName(title, 200) + "." : ".";
        return subdirectory + zeroPad(index) + safeTitle + extension;
    }

    /**
     * Check if an element is a text area field.
     *
     * @param element The element to check.
     * @return True if the element is a text area field, false otherwise.
     */
    public static boolean isTextAreaField(Element element) {
        return "TEXTAREA".equalsIgnoreCase(element.tagName());
    }

    /**
     * Check if an element is a text input field.
     *
     * @param element The element to check.
     * @return True if the element is a text input field, false otherwise.
     */
    public static boolean isTextInputField(Element element) {
        return "INPUT".equalsIgnoreCase(element.tagName()) &&
                ("text".equalsIgnoreCase(element.attr("type")) || "url".equalsIgnoreCase(element.attr("type")));
    }
    /**
     * Check if XHTML content is invalid.
     *
     * @param xhtmlAsString The XHTML content as a string.
     * @param mimeType      The MIME type of the content.
     * @return The error message if the content is invalid, or null if it's valid.
     */
    public static boolean isXhtmlInvalid(String xhtmlAsString, String mimeType) {
        try {
            var doc = Jsoup.parse(xhtmlAsString);
            doc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Convert DC terms to a table element.
     *
     * @param dom The document containing the DC terms.
     * @return A table element representing the DC terms.
     */
    public static Element dctermsToTable(Document dom) {
        Element table = dom.createElement("table");
        Element body = dom.createElement("tbody");
        table.appendChild(body);
        Elements metaTags = dom.select("meta");
        for (int i = 0; i < metaTags.size(); i++) {
            Element meta = (Element) metaTags.get(i);
            if (meta.attr("name").startsWith("dcterms.")) {
                Element row = dom.createElement("tr");
                body.appendChild(row);
                Element td = dom.createElement("td");
                row.appendChild(td);
                td.text(meta.attr("name").replace("dcterms.", ""));
                td = dom.createElement("td");
                row.appendChild(td);
                td.text(meta.attr("content"));
            }
        }
        return table;
    }

    /**
     * Parse HTML content and insert it's body into(and replacing) the passed in content.
     *
     * @param htmlText The HTML text to parse.
     * @param content  The element where the parsed content should be inserted.
     */
    public static void parseHtmlAndInsertIntoContent(String htmlText, Element content) {
        var parsed = Jsoup.parse(htmlText);
        Element body = (Element) parsed.select("body").get(0);
        content.replaceWith(body);
    }

    /**
     * Get the file name for a style sheet.
     *
     * @return The file name of the style sheet.
     */
    public static String styleSheetFileName() {
        return "OEBPS/Styles/stylesheet.css";
    }

    /**
     * Extract a substring between a prefix and suffix in a string.
     *
     * @param s       The string to search in.
     * @param prefix  The prefix to start the extraction.
     * @param suffix  The suffix to end the extraction.
     * @return The extracted substring or null if not found.
     */
    public static String extactSubstring(String s, String prefix, String suffix) {
        int startIndex = s.indexOf(prefix);
        if (startIndex < 0) {
            return null;
        }
        startIndex += prefix.length();
        int endIndex = s.indexOf(suffix, startIndex);
        if (endIndex < 0) {
            return null;
        }
        return s.substring(startIndex, endIndex);
    }

    /**
     * Extract a substring between a prefix and suffix in a string,
     * not including the prefix and suffix.
     *
     * @param s       The string to search in.
     * @param prefix  The prefix to start the extraction.
     * @param suffix  The suffix to end the extraction.
     * @return The extracted substring or null if not found.
     */
    public static String extractSubstringExclusive(String s, String prefix, String suffix) {
        String substring = extactSubstring(s, prefix, suffix);
        return substring.substring(prefix.length(), substring.length() - suffix.length());
    }

    /**
     * Find the index of the closing quote in a string.
     *
     * @param s           The string to search in.
     * @param startIndex  The start index to search from.
     * @return The index of the closing quote or -1 if not found.
     */
    public static int findIndexOfClosingQuote(String s, int startIndex) {
        int index = startIndex;
        while (index < s.length() && s.charAt(index) != '"') {
            if (s.charAt(index) == '\\' && index + 1 < s.length()) {
                index++; // Skip escaped characters
            }
            index++;
        }
        return index < s.length() ? index : -1;
    }

    /**
     * Find the index of the closing bracket in a string.
     *
     * @param s           The string to search in.
     * @param startIndex  The start index to search from.
     * @return The index of the closing bracket or -1 if not found.
     */
    public static int findIndexOfClosingBracket(String s, int startIndex) {
        int depth = 1;
        for (int i = startIndex + 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ']' || c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            } else if (c == '[' || c == '{') {
                depth++;
            } else if (c == '"' && i + 1 < s.length()) {
                i = findIndexOfClosingQuote(s, i);
            }
        }
        return -1;
    }
    /**
     * Locate and extract JSON embedded in a string.
     *
     * @param s       The string containing the JSON.
     * @param prefix  The prefix before the JSON.
     * @return The extracted JSON or null if not found.
     */
    public static String locateAndExtractJson(String s, String prefix) {
        int startIndex = s.indexOf(prefix);
        if (startIndex >= 0) {
            startIndex = findOpeningBracket(s, startIndex + prefix.length());
            if (startIndex >= 0) {
                int endIndex = findIndexOfClosingBracket(s, startIndex);
                if (endIndex > startIndex) {
                    return s.substring(startIndex, endIndex + 1);
                }
            }
        }
        return null;
    }

    /**
     * Find the index of the opening bracket in a string.
     *
     * @param s         The string to search in.
     * @param startIndex The start index to search from.
     * @return The index of the opening bracket or -1 if not found.
     */
    private static int findOpeningBracket(String s, int startIndex) {
        for (int i = startIndex; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '[' || ch == '{') {
                return i;
            }
        }
        return -1;
    }


// The methods findIndexOfClosingBracket and findOpeningBracket are reused from previous conversions.




}

