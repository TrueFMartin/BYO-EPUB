package com.folioreader.builder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class ContentFetcher {

    public ParseResult fetchContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String htmlContent = doc.outerHtml();
        return new ParseResult("ParseResults", htmlContent, url);
    }

    static class ParseResult {
        String messageType;
        String document;
        String url;

        public ParseResult(String messageType, String document, String url) {
            this.messageType = messageType;
            this.document = document;
            this.url = url;
        }

    }
}