package com.folioreader.builder;


import com.folioreader.builder.parsers.RubymaybetranslationsParser;
import com.folioreader.builder.parsers.VolarenovelsParser;
import com.folioreader.builder.parsers.WanderinginnParser;
import com.folioreader.builder.parsers.WordexcerptParser;
import com.folioreader.builder.parsers.WuxiaworldParser;
import com.folioreader.builder.parsers.WuxiaworldWorldParser;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ParserFactory {
    private Map<String, Function<Void, Parser>> parsers;
    private List<ParserRule> parserRules;
    private List<ParserUrlRule> parserUrlRules;
    private List<ManualSelection> manualSelection;

    public ParserFactory() {
        this.parsers = new HashMap<String, Function<Void, Parser>>();
        this.parserRules = new ArrayList<>();
        this.parserUrlRules = new ArrayList<>();
        this.manualSelection = new ArrayList<>();
        this.registerManualSelect("", (a) -> {
            return null;
        });
        register("wuxiaworld.com", WuxiaworldParser::new);
        register("wuxiaworld.world", WuxiaworldWorldParser::new);
        register("thewanderinginn.com", WanderinginnParser::new);
        register("wordexcerpt.com", WordexcerptParser::new);
        register("volarenovels.com", VolarenovelsParser::new);
        register("rubymaybetranslations.com", RubymaybetranslationsParser::new);


    }

    public static boolean isWebArchive(String url) {
        String host = Util.extractHostName(url);
        String[] subs = {"web", "web-beta"};
        for (String sub : subs) {
            if (host.startsWith(sub + ".archive.org")) {
                return true;
            }
        }
        return false;
    }

    public static String stripWebArchive(String url) {
        String[] hostName = url.split("://");
        return hostName.length > 2 ? "https://" + hostName[2] : url;
    }

    public static String stripLeadingWww(String hostName) {
        return hostName.startsWith("www.") ? hostName.substring(4) : hostName;
    }

    public static Parser getParser(String url, Document dom) {
        String s = stripWebArchive(stripLeadingWww(url));
        return new WanderinginnParser();
    }

    public void register(String hostName, Function<Void, Parser> constructor) {
        String strippedHostName = ParserFactory.stripLeadingWww(hostName);
        if (!this.parsers.containsKey(strippedHostName)) {
            this.parsers.put(strippedHostName, constructor);
        } else {
            throw new IllegalArgumentException("Duplicate parser registered for hostName " + hostName);
        }
    }

    public void reregister(String hostName, Function<Void, Parser> constructor) {
        this.parsers.put(ParserFactory.stripLeadingWww(hostName), constructor);
    }

    public void registerManualSelect(String name, Function<Void, Parser> constructor) {
        this.manualSelection.add(new ManualSelection(name, constructor));
    }

    public void registerRule(Function<TwoArgTest, Boolean> test, Function<String, Parser> constructor) {
        this.parserRules.add(new ParserRule(test, constructor));
    }

    public void registerUrlRule(Function<String, Boolean> test, Function<String, Parser> constructor) {
        this.parserUrlRules.add(new ParserUrlRule(test, constructor));
    }

    // Method implementations for fetchByUrl, fetch, addParsersToPages, etc.

    private static class ManualSelection {
        String name;
        Function<Void, Parser> constructor;

        public ManualSelection(String name, Function<Void, Parser> constructor) {
            this.name = name;
            this.constructor = constructor;
        }
    }

    private static class ParserRule {
        Function<TwoArgTest, Boolean> test;
        Function<String, Parser> constructor;

        public ParserRule(Function<TwoArgTest, Boolean> test, Function<String, Parser> constructor) {
            this.test = test;
            this.constructor = constructor;
        }
    }

    private static class ParserUrlRule {
        Function<String, Boolean> test;
        Function<String, Parser> constructor;

        public ParserUrlRule(Function<String, Boolean> test, Function<String, Parser> constructor) {
            this.test = test;
            this.constructor = constructor;
        }
    }

    // Additional nested classes and methods as required...

    // Helper class for two-argument test
    private static class TwoArgTest {
        String url;
        Document dom; // Assuming Document is from an HTML parsing library

        public TwoArgTest(String url, Document dom) {
            this.url = url;
            this.dom = dom;
        }
    }
}

