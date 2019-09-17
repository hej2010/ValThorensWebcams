package se.swecookie.valthorens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class Preview {
    private String previewUrl;
    private boolean loading, gotPreview;
    private Webcam webcam;

    Preview(@NonNull Webcam webcam) {
        this.previewUrl = webcam.previewUrl == null ? null : webcam.previewUrl.replace("http:", "https:");
        loading = false;
        gotPreview = false;
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

    boolean gotPreview() {
        return gotPreview;
    }

    void setGotPreview() {
        this.gotPreview = true;
    }

    Webcam getWebcam() {
        return webcam;
    }
}
