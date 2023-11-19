package com.folioreader.builder;

public class VariableSizeImageCollector extends ImageCollector {

    public VariableSizeImageCollector() {
        super();
    }

    @Override
    public void onUserPreferencesUpdate(UserPreferences userPreferences) {
        super.onUserPreferencesUpdate(userPreferences);
        if (userPreferences.isHighestResolutionImages()) {
            this.initialUrlToTry = (ImageInfo imageInfo) -> imageInfo.getWrappingUrl();
        } else {
            this.initialUrlToTry = (ImageInfo imageInfo) -> imageInfo.getSourceUrl();
        }
    }

    // The method initialUrlToTry is used to select the appropriate URL for an image
    // based on the user's preferences.
    private ImageUrlSelector initialUrlToTry;

    // Additional methods or logic as required...

    @FunctionalInterface
    private interface ImageUrlSelector {
        String selectUrl(ImageInfo imageInfo);
    }
}
