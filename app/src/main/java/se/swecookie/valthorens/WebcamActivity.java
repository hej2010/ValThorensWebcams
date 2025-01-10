package se.swecookie.valthorens;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.text.DecimalFormat;

import se.swecookie.valthorens.data.Webcam;
import se.swecookie.valthorens.databinding.ActivityWebcam2Binding;

public class WebcamActivity extends AppCompatActivity implements IOnImageDownloaded {
    private static final String TAG = "WebcamActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    public static final String EXTRA_WEBCAM = "w";


    private ActivityWebcam2Binding binding;
    private ImageDownloader imageDownloader;
    private final DecimalFormat formater = new DecimalFormat("#.##");

    private boolean focused, showMessages, showDownload;
    private Webcam clickedWebcam;
    private Toast toast;
    private String imageUrl, imageDate;
    private AsyncTask<Void, Void, Pair<Uri, Exception>> asyncTask;

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebcam2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedWebcam = (Webcam) extras.getSerializable(EXTRA_WEBCAM);
        } else {
            showToast(getString(R.string.error));
            finish();
            return;
        }

        binding.lLMessage.setVisibility(View.GONE);
        binding.imgDownload.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        showMessages = prefs.getBoolean(AboutActivity.PREFS_MESSAGES_KEY, true);
        showDownload = prefs.getBoolean(AboutActivity.PREFS_DOWNLOADS_KEY, false);

        binding.lLMessage.setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            binding.txtTitle.setText("");
            binding.txtBody.setText("");
            if (hasNotShownMessageInfo()) {
                showToast(getString(R.string.webcam_message_info));
                setHasShownMessageInfo();
            }
        });

        setTitleToCameraName();

        // TODO Les Menuires: https://lesmenuires.com/webcams/
        // TODO Meribel: https://ski-resort.meribel.net/all-the-webcam.html

        focused = false;

        binding.imgWebcam.setOnClickListener(view -> {
            if (!focused) {
                binding.imgDownload.setVisibility(View.GONE);
                binding.txtDate.setVisibility(View.GONE);
                binding.txtWebCamTitle.setVisibility(View.GONE);
                binding.lLMessage.setVisibility(View.GONE);
            } else {
                if (showDownload) {
                    binding.imgDownload.setVisibility(View.VISIBLE);
                }
                binding.txtDate.setVisibility(View.VISIBLE);
                binding.txtWebCamTitle.setVisibility(View.VISIBLE);
                if (showMessages && (!binding.txtBody.getText().toString().isEmpty()
                        || !binding.txtTitle.getText().toString().isEmpty())) {
                    binding.lLMessage.setVisibility(View.VISIBLE);
                }
            }
            focused = !focused;
            toggleFullscreen(WebcamActivity.this);
        });
    }

    private void showToast(String string) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean hasNotShownMessageInfo() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        return !prefs.getBoolean(AboutActivity.PREFS_HAS_SHOWN_MESSAGE_INFO_KEY, false);
    }

    private void setHasShownMessageInfo() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(AboutActivity.PREFS_HAS_SHOWN_MESSAGE_INFO_KEY, true).apply();
    }

    private void toggleFullscreen(AppCompatActivity activity) {
        int newUiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void setTitleToCameraName() {
        binding.txtWebCamTitle.setText(clickedWebcam.name);
        getImage(clickedWebcam);
    }

    private void getImage(Webcam webcam) {
        if (imageDownloader == null) {
            imageDownloader = new ImageDownloader();
        } else {
            imageDownloader.cancel();
        }
        binding.txtDate.setVisibility(View.INVISIBLE);
        imageDownloader.startDownload(webcam, WebcamActivity.this, this);
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void onDownloadClicked(View view) {
        if (view.getId() == R.id.imgDownload) {
            if (isStoragePermissionGranted(this)) {
                downloadImage();
            } else {
                showPermissionNeededDialog();
            }
        }
    }

    public static boolean isStoragePermissionGranted(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    private void showPermissionDeniedToast() {
        showToast(getString(R.string.webcam_download_permission_denied));
    }

    private void downloadImage() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setDescription(binding.txtDate.getText().toString());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = clickedWebcam.name
                + "-"
                + (imageDate == null ? System.currentTimeMillis() : imageDate)
                + ".jpg";
        fileName = fileName.replace(" ", "_").replace(":", "");
        Log.e(TAG, "downloadImage: uri: " + Uri.parse(imageUrl) + " .... " + imageUrl + " with file name: " + fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setTitle(fileName);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            showToast(getString(R.string.downloading));
            binding.imgDownload.setVisibility(View.GONE);
        } else {
            showToast(getString(R.string.error));
            Log.e(TAG, "downloadImage: error");
        }
    }

    @Override
    public void onImageInfoFound(String currentURL, int height, String imageDate, String title, String body, String errorMessage) {
        if (currentURL == null || currentURL.isEmpty()) {
            showErrorDialog(errorMessage);
        } else {
            if (showDownload) {
                binding.imgDownload.setVisibility(View.VISIBLE);
            }
            currentURL = currentURL.replace("http:", "https:");
            this.imageUrl = currentURL;
            if (imageDate != null && imageDate.startsWith("Taken at")) {
                this.imageDate = imageDate.split("Taken at ")[1];
            }

            Log.e(TAG, "onImageInfoFound: " + currentURL + "; date: " + imageDate);

            asyncTask = new DownloadFileFromURL(this, currentURL, this).execute();
            binding.imgWebcam.setVisibility(View.VISIBLE);
            binding.txtDate.setText(imageDate);
            binding.txtDate.setVisibility(View.VISIBLE);

            if (showMessages) {
                boolean showTitle = false, showBody = false;
                if (!title.isEmpty() && !title.equals("null")) {
                    binding.txtTitle.setText(title);
                    showTitle = true;
                }
                if (!body.isEmpty()) {
                    binding.txtBody.setText(body);
                    showBody = true;
                }
                if (showTitle || showBody) {
                    binding.lLMessage.setVisibility(View.VISIBLE);
                    if (!showTitle) {
                        binding.txtTitle.setVisibility(View.GONE);
                    }
                    if (!showBody) {
                        binding.txtBody.setVisibility(View.GONE);
                    }
                } else {
                    binding.lLMessage.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onImageProgress(int progress, long total, long lengthOfFile) {
        binding.progressBar.setProgress(progress);
        double d1 = (total + 0.0) / 1000000;
        double max = (lengthOfFile + 0.0) / 1000000;
        runOnUiThread(() -> binding.txtProgress.setText(getString(R.string.webcam_progress, progress, formater.format(d1), formater.format(max))));
    }

    @Override
    public void onImageSavedToCache(Uri file, Exception e) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isFinishing() || isDestroyed()) {
                return;
            }
        } else {
            if (isFinishing()) {
                return;
            }
        }
        if (file != null) {
            binding.imgWebcam.setImage(ImageSource.uri(file.toString()));
            binding.imgWebcam.setMinimumDpi(60);
            binding.loadingPanel.setVisibility(View.GONE);
            binding.imgWebcam.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                @Override
                public void onImageLoadError(Exception e) {
                    super.onImageLoadError(e);
                    e.printStackTrace();
                    Log.e(TAG, "onImageLoadError: failed to load " + e.getMessage());
                    showErrorDialog(e.getMessage());
                }
            });
        } else {
            showErrorDialog(e.getMessage());
        }
    }

    private void showErrorDialog(@Nullable String errorMessage) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        String message = getString(R.string.webcam_load_error);
        if (errorMessage != null) {
            message = message + "Error: " + errorMessage;
        }
        if (isFinishing() || isDestroyed()) {
            return;
        }
        builder.setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showPermissionNeededDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.webcam_download_permission_needed_title))
                .setMessage(getString(R.string.webcam_download_permission_needed_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> askForPermission())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                downloadImage();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                showPermissionDeniedToast();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        if (imageDownloader != null) {
            imageDownloader.cancel();
        }
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        super.onBackPressed();
    }
}
