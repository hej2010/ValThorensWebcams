package se.swecookie.valthorens.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Preview {
    private String previewUrl;
    private boolean loading, gotPreviewUrl, previewShown, notFound;
    private Webcam webcam;

    public Preview(@NonNull Webcam webcam) {
        this.previewUrl = webcam.previewUrl == null ? null : webcam.previewUrl.replace("http:", "https:");
        loading = false;
        gotPreviewUrl = false;
        this.webcam = webcam;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setPreviewUrl(@NonNull String previewUrl) {
        this.previewUrl = previewUrl.replace("http:", "https:");
    }

    @Nullable
    public String getPreviewUrl() {
        return previewUrl;
    }

    public boolean isNotLoading() {
        return !loading;
    }

    public boolean gotPreviewUrl() {
        return gotPreviewUrl;
    }

    public void setGotPreview() {
        this.gotPreviewUrl = true;
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public boolean hasPreviewBeenShown() {
        return previewShown;
    }

    public void setPreviewShown(boolean previewShown) {
        this.previewShown = previewShown;
    }

    public boolean isNotFound() {
        return notFound;
    }

    public void setNotFound() {
        this.notFound = true;
    }
}
