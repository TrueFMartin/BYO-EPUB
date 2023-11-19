package com.folioreader.builder;

public class EpubMetaInfo {

    private String uuid;
    private String title;
    private String author;
    private String language;
    private String fileName;
    private String subject;
    private String description;
    private String seriesName;
    private String seriesIndex;
    private String styleSheet;
    private String translator;
    private String fileAuthorAs;

    public EpubMetaInfo() {
        this.uuid = "defaultUUID"; // Replace with actual default value or method to generate it
        this.title = "defaultTitle"; // Replace with actual default value
        this.author = "defaultAuthor"; // Replace with actual default value
        this.language = "en";
        this.fileName = "web.epub";
        this.subject = "";
        this.description = "";
        this.seriesName = null;
        this.seriesIndex = null;
        this.styleSheet = getDefaultStyleSheet();
        this.translator = null;
        this.fileAuthorAs = null;
    }

    public String getFileAuthorAs() {
        return (this.fileAuthorAs == null) ? this.author : this.fileAuthorAs;
    }

    public static String getDefaultStyleSheet() {
        return "" +
                // CSS styles as defined in the original JS file
                "div.svg_outer {...}\n" +
                // Additional CSS styles...
                "table, th, td {...}";
    }

    // Getter and setter methods for the class fields

    // Additional methods as needed...
}
