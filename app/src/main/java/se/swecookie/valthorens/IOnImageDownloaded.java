package se.swecookie.valthorens;

interface IOnImageDownloaded {
    void onImageDownloaded(String currentURL, int height, String imageDate, String title, String body, String errorMessage);
}
