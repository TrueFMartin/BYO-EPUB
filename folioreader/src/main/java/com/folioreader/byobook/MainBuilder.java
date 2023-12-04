package com.folioreader.byobook;

public class MainBuilder {
    String url;
    public MainBuilder(String url) {
        this.url = url;
    }

    public void build() {
        var contentFetcher = new ContentFetcher();

    }
}
