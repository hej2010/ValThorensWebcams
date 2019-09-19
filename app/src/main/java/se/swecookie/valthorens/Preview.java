package se.swecookie.valthorens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class Preview {
    private String previewUrl;
    private boolean loading, gotPreviewUrl, previewShown, notFound;
    private Webcam webcam;

    Preview(@NonNull Webcam webcam) {
        this.previewUrl = webcam.previewUrl == null ? null : webcam.previewUrl.replace("http:", "https:");
        loading = false;
        gotPreviewUrl = false;
        this.webcam = webcam;
    }

    void setLoading(boolean loading) {
        this.loading = loading;
    }

    void setPreviewUrl(@NonNull String previewUrl) {
        this.previewUrl = previewUrl.replace("http:", "https:");
    }

    @Nullable
    String getPreviewUrl() {
        return previewUrl;
    }

    boolean isNotLoading() {
        return !loading;
    }

    boolean gotPreviewUrl() {
        return gotPreviewUrl;
    }

    void setGotPreview() {
        this.gotPreviewUrl = true;
    }

    Webcam getWebcam() {
        return webcam;
    }

    boolean hasPreviewBeenShown() {
        return previewShown;
    }

    void setPreviewShown(boolean previewShown) {
        this.previewShown = previewShown;
    }

    boolean isNotFound() {
        return notFound;
    }

    void setNotFound() {
        this.notFound = true;
    }
}
