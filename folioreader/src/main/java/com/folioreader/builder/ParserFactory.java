package com.folioreader.builder;


import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ParserFactory {
    private Map<String, Function<String, Parser>> parsers;
    private List<ParserRule> parserRules;
    private List<ParserUrlRule> parserUrlRules;
    private List<ManualSelection> manualSelection;

    public ParserFactory() {
        this.parsers = new HashMap<>();
        this.parserRules = new ArrayList<>();
        this.parserUrlRules = new ArrayList<>();
        this.manualSelection = new ArrayList<>();
        this.registerManualSelect("", () -> null);
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

    public void register(String hostName, Function<String, Parser> constructor) {
        String strippedHostName = ParserFactory.stripLeadingWww(hostName);
        if (!this.parsers.containsKey(strippedHostName)) {
            this.parsers.put(strippedHostName, constructor);
        } else {
            throw new IllegalArgumentException("Duplicate parser registered for hostName " + hostName);
        }
    }

    public void reregister(String hostName, Function<String, Parser> constructor) {
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

