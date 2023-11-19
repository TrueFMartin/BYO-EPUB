package com.folioreader.builder;

import java.util.ArrayList;
import java.util.List;

public class EpubItemSupplier {

    private Parser parser;
    private List<EpubItem> epubItems;
    private ImageCollector imageCollector;
    private CoverImageInfo coverImageInfo;

    public EpubItemSupplier(Parser parser, List<EpubItem> epubItems, ImageCollector imageCollector) {
        this.parser = parser;
        this.epubItems = new ArrayList<>(epubItems); // Assuming EpubItem is a class representing an item to be packed
        this.imageCollector = imageCollector;
        this.coverImageInfo = imageCollector.getCoverImageInfo(); // Assuming this method exists in ImageCollector

        // Add images to pack in EPUB
        this.epubItems.addAll(imageCollector.imagesToPackInEpub());
    }

    // Used to populate manifest
    public List<EpubItem> manifestItems() {
        return this.epubItems;
    }

    // Used to populate spine
    public List<EpubItem> spineItems() {
        List<EpubItem> spineItems = new ArrayList<>();
        for (EpubItem item : this.epubItems) {
            if (item.isInSpine()) { // Assuming isInSpine is a method in EpubItem
                spineItems.add(item);
            }
        }
        return spineItems;
    }

    // Used to populate Zip file itself
    public List<EpubItem> files() {
        return this.epubItems;
    }

    // Used to populate table of contents
    public List<ChapterInfo> chapterInfo() { // Assuming ChapterInfo is a class representing chapter information
        List<ChapterInfo> chapters = new ArrayList<>();
        for (EpubItem item : this.epubItems) {
            chapters.addAll(item.chapterInfo()); // Assuming chapterInfo returns a list of ChapterInfo objects
        }
        return chapters;
    }

    public String makeCoverImageXhtmlFile() { // The method to create an XHTML file for the cover image
        // This method's implementation will depend on how you handle XHTML file creation in Java
        return ""; // Placeholder for the generated XHTML content
    }

    public boolean hasCoverImageFile() {
        return this.coverImageInfo != null;
    }

    // Additional methods as needed...

    // Getter and setter methods for the class fields

}
