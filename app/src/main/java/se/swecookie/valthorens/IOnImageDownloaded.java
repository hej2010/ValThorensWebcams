package se.swecookie.valthorens;

import android.net.Uri;

interface IOnImageDownloaded {
    void onImageInfoFound(String currentURL, int height, String imageDate, String title, String body, String errorMessage);
    void onImageSavedToCache(Uri file, Exception e);
    void onImageProgress(int progress, long total, long lengthOfFile);
}
