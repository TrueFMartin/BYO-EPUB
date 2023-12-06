package com.folioreader.byobook;

import com.folioreader.builder.Parser;
import com.folioreader.builder.ParserFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class ContentFetcher {


    public ParseResult fetchContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return new ParseResult(doc, url);
    }

    public ParseResult fetchContentInitial(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        var parser = new ParserFactory().getParser(url);
        return new ParseResult(doc, url, parser);
    }


    public class ParseResult {
        Document document;
        String url;
        Parser parser;
        public int order;

        public ParseResult(Document document, String url) {
            this.document = document;
            this.url = url;
        }

        public ParseResult(Document document, String url, Parser parser) {
            this.document = document;
            this.url = url;
            this.parser = parser;
        }
    }
}