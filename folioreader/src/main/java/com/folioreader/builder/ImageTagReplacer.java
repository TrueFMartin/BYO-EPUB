package com.folioreader.builder;


import org.jsoup.nodes.Element;

public class ImageTagReplacer {
    private Element wrappingElement;
    private String wrappingUrl;
    private UserPreferences userPreferences;

    public ImageTagReplacer(Element wrappingElement, String wrappingUrl, UserPreferences userPreferences) {
        this.wrappingElement = wrappingElement;
        this.wrappingUrl = wrappingUrl;
        this.userPreferences = userPreferences;
    }

    public void replaceTag(ImageInfo imageInfo) {
        Element parent = wrappingElement.parent();
        if (imageInfo != null && parent != null) {
            if (isDuplicateImageToRemove(imageInfo)) {
                wrappingElement.remove();
            } else {
                insertImageInLegalParent(parent, imageInfo);
            }
        }
    }

    private void insertImageInLegalParent(Element parent, ImageInfo imageInfo) {
        if (isImageInline(imageInfo)) {
            insertInlineImageInLegalParent(imageInfo);
        } else {
            insertBlockImageInLegalParent(parent, imageInfo);
        }
    }

    private boolean isImageInline(ImageInfo imageInfo) {
        final int MAX_INLINE_IMAGE_HEIGHT = 200;
        Element parent = wrappingElement;
        while (parent != null && Util.isInlineElement(parent)) {
            parent = parent.parent();
        }
        return isParagraph(parent) && !Util.isNullOrEmpty(parent.text()) &&
                imageInfo.getHeight() <= MAX_INLINE_IMAGE_HEIGHT;
    }

    private boolean isParagraph(Element element) {
        return element != null && "p".equalsIgnoreCase(element.tagName());
    }

    private void insertInlineImageInLegalParent(ImageInfo imageInfo) {
        Element newImage = imageInfo.createImgImageElement("span");
        wrappingElement.replaceWith(newImage);
    }

    private void insertBlockImageInLegalParent(Element parent, ImageInfo imageInfo) {
        Element nodeAfter = wrappingElement;
        while (Util.isInlineElement(parent) && parent.parent() != null) {
            nodeAfter = parent;
            parent = parent.parent();
        }
        if (isParagraph(parent)) {
            nodeAfter = parent;
        }
        Element newImage = imageInfo.createImageElement(userPreferences);
        nodeAfter.before(newImage);
        Util.removeHeightAndWidthStyleFromParents(newImage);
        copyCaption(newImage, wrappingElement);
        wrappingElement.remove();
    }

    private void copyCaption(Element newImage, Element oldWrapper) {
        Element thumbCaption = oldWrapper.selectFirst("div.thumbcaption");
        if (thumbCaption != null) {
            for (Element magnify : thumbCaption.select("div.magnify")) {
                magnify.remove();
            }
            if (!Util.isNullOrEmpty(thumbCaption.text())) {
                newImage.appendChild(thumbCaption);
            }
        }
    }

    private boolean isDuplicateImageToRemove(ImageInfo imageInfo) {
        return userPreferences.isRemoveDuplicateImages() &&
                isElementInImageGallery() && (imageInfo.isOutsideGallery() || imageInfo.isCover());
    }

    private boolean isElementInImageGallery() {
        return "thumb".equals(wrappingElement.className());
    }
}
