package com.folioreader.builder;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Sanitize {

    private static final Map<String, String[]> TAG_ATTRIBUTES = createTagAttributesMap();
    private static final Pattern INVALID_CHARS_REGEX = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");

    public Element clean(Element element) {
        Element clonedElement = cloneTag(element);
        for (Node child : element.childNodes()) {
            Node clonedChild = cloneChildNode(child);
            if (clonedChild != null) {
                clonedElement.appendChild(clonedChild);
            }
        }
        return clonedElement;
    }

    private Element cloneTag(Element element) {
        String tag = element.tagName().toLowerCase();
        String[] validAttributes = TAG_ATTRIBUTES.getOrDefault(tag, new String[]{"id", "class", "style"});
        Element newElement = element.ownerDocument().createElement(tag);
        for (String attribute : validAttributes) {
            String val = element.attr(attribute);
            if (!val.isEmpty()) {
                newElement.attr(attribute, val);
            }
        }
        return newElement;
    }

    private Node cloneChildNode(Node node) {
        if (node instanceof Element) {
            return clean((Element) node);
        } else if (node instanceof TextNode) {
            return cleanTextNode((TextNode) node);
        } else {
            return node.clone(); // For comments and other types
        }
    }

    private TextNode cleanTextNode(TextNode textNode) {
        String text = stripInvalidCharsFromString(textNode.getWholeText());
        return new TextNode(text, textNode.baseUri());
    }

    private static String stripInvalidCharsFromString(String s) {
        return INVALID_CHARS_REGEX.matcher(s).replaceAll("");
    }

    private static Map<String, String[]> createTagAttributesMap() {
        Map<String, String[]> map = new HashMap<>();
        // Add specific tag attributes here. Example:
        map.put("a", new String[]{"href"});
        map.put("img", new String[]{"src"});
        // Add more tags and their attributes as needed
        return map;
    }
}
