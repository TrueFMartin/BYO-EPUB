package com.folioreader.builder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImageCollector {
    private List<ImageInfo> imageInfoList;
    private Map<String, Integer> urlIndex;
    private Map<String, Integer> bitmapIndex;
    private List<ImageInfo> imagesToFetch;
    private List<ImageInfo> imagesToPack;
    private ImageInfo coverImageInfo;
    private UserPreferences userPreferences; // Assuming UserPreferences is a class holding user settings

    public ImageCollector() {
        reset();
        this.userPreferences = null;
    }

    // Static method to return a stub collector for parsers with no images
    public static ImageCollector stubCollector() {
        return new ImageCollector() {
            @Override
            public List<ImageInfo> imagesToPackInEpub() {
                return new ArrayList<>();
            }
        };
    }

    public void reset() {
        this.imageInfoList = new ArrayList<>();
        this.urlIndex = new HashMap<>();
        this.bitmapIndex = new HashMap<>();
        this.imagesToFetch = new ArrayList<>();
        this.imagesToPack = new ArrayList<>();
        this.coverImageInfo = null;
    }

    public void copyState(ImageCollector other) {
        this.imageInfoList = other.imageInfoList;
        this.urlIndex = other.urlIndex;
        this.bitmapIndex = other.bitmapIndex;
        this.imagesToFetch = other.imagesToFetch;
        this.imagesToPack = other.imagesToPack;
        this.coverImageInfo = other.coverImageInfo;
        this.userPreferences = other.userPreferences;
    }

    // Other methods for handling images like addImageInfo, setCoverImageUrl, imageInfoByUrl, etc.

    // Placeholder for methods to fetch images, calculate hash, etc.

    public List<ImageInfo> imagesToPackInEpub() {
        return this.imagesToPack;
    }


    public ImageInfo addImageInfo(String wrappingUrl, String sourceUrl, String dataOrigFileUrl, boolean fetchFirst) {
        ImageInfo imageInfo = null;
        Integer index = this.urlIndex.get(sourceUrl);
        if (index == null) {
            index = this.urlIndex.get(wrappingUrl);
        }
        if (index == null) {
            index = this.urlIndex.get(dataOrigFileUrl);
        }
        if (index != null) {
            imageInfo = this.imageInfoList.get(index);
        } else {
            index = this.imageInfoList.size();
            imageInfo = new ImageInfo(wrappingUrl, index, sourceUrl, dataOrigFileUrl);
            this.imageInfoList.add(imageInfo);
            if (fetchFirst) {
                this.imagesToFetch.add(0, imageInfo);
            } else {
                this.imagesToFetch.add(imageInfo);
            }
        }
        this.urlIndex.put(wrappingUrl, index);
        this.urlIndex.put(sourceUrl, index);
        if (dataOrigFileUrl != null) {
            this.urlIndex.put(dataOrigFileUrl, index);
        }
        return imageInfo;
    }


    public ImageInfo imageInfoByUrl(String url) {
        Integer index = this.urlIndex.get(url);
        return (index == null) ? null : this.imageInfoList.get(index);
    }

    // Method to handle user preferences update
    public void onUserPreferencesUpdate(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    // Method to return the number of images to fetch
    public int numberOfImagesToFetch() {
        return this.imagesToFetch.size();
    }


    private String initialUrlToTry(ImageInfo imageInfo) {
        return imageInfo.chapterTitle;
    }

    public static boolean urlHasFragment(String url) {
        try {
            return new URL(url).getRef() != null && !new URL(url).getRef().isEmpty();
        } catch (Exception error) {
            return false;
        }
    }

    public static String removeSizeParamsFromWordPressQuery(String originalUrl) {
        return originalUrl;
    }

    public static boolean isWordPressHostedFile(String hostname) {
        return hostname.endsWith("files.wordpress.com") || hostname.endsWith(".wp.com");
    }



        public String selectImageUrlFromImagePage(Document dom) {
            Element div = dom.selectFirst("div.fullMedia");
            if (div != null) {
                Element link = div.selectFirst("a");
                return link != null ? link.attr("href") : null;
            }
            return null;
        }


        public static Document replaceHyperlinksToImagesWithImages(Document content, String parentPageUrl) {
            Elements toReplace = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                toReplace = content.select("a").stream()
                        .filter(hyperlink -> isHyperlinkToImage(hyperlink))
                        .filter(hyperlink -> !linkContainsImageTag(hyperlink))
                        .collect(Collectors.toCollection(Elements::new));
            }
            for (Element hyperlink : toReplace) {
                replaceHyperlinkWithImg(hyperlink);
            }
            return content;
        }

        private static boolean isHyperlinkToImage(Element hyperlink) {
            String extension = getExtensionFromUrlFilename(hyperlink);
            return "png".equals(extension) || "jpg".equals(extension) ||
                    "jpeg".equals(extension) || "gif".equals(extension) ||
                    "svg".equals(extension);
        }

        private static String getExtensionFromUrlFilename(Element hyperlink) {
            String[] split = hyperlink.attr("href").split("\\.");
            return split.length < 2 ? "" : split[split.length - 1];
        }

        private static boolean linkContainsImageTag(Element hyperlink) {
            return hyperlink.selectFirst("img") != null;
        }

        private static void replaceHyperlinkWithImg(Element hyperlink) {
            Element img = hyperlink.ownerDocument().createElement("img");
            img.attr("src", hyperlink.attr("href"));
            hyperlink.replaceWith(img);
        }


    // Additional nested classes and helper methods...
}

