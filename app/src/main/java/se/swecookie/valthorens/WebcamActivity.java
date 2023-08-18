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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.text.DecimalFormat;

import se.swecookie.valthorens.data.Webcam;

public class WebcamActivity extends AppCompatActivity implements IOnImageDownloaded {
    private static final String TAG = "WebcamActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    public static final String EXTRA_WEBCAM = "w";

    private TextView txtWebCamTitle, txtDate, txtTitle, txtBody;
    private SubsamplingScaleImageView imgWebcam;
    private ImageView imgDownload;
    private RelativeLayout loadingPanel;
    private ImageDownloader imageDownloader;
    private LinearLayout lLMessage;
    private ProgressBar progressBar;
    private TextView txtProgress;
    private final DecimalFormat formater = new DecimalFormat("#.##");

    private boolean focused, showMessages, showDownload;
    //private Snackbar snackbar = null;
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
        setContentView(R.layout.activity_webcam2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedWebcam = (Webcam) extras.getSerializable(EXTRA_WEBCAM);
        } else {
            showToast(getString(R.string.error));
            finish();
            return;
        }

        txtWebCamTitle = findViewById(R.id.txtWebCamTitle);
        imgWebcam = findViewById(R.id.imgWebcam);
        txtDate = findViewById(R.id.txtDate);
        lLMessage = findViewById(R.id.lLMessage);
        txtTitle = findViewById(R.id.txtTitle);
        txtBody = findViewById(R.id.txtBody);
        imgDownload = findViewById(R.id.imgDownload);
        lLMessage.setVisibility(View.GONE);
        imgDownload.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        txtProgress = findViewById(R.id.txtProgress);
        loadingPanel = findViewById(R.id.loadingPanel);

        SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        showMessages = prefs.getBoolean(AboutActivity.PREFS_MESSAGES_KEY, true);
        showDownload = prefs.getBoolean(AboutActivity.PREFS_DOWNLOADS_KEY, false);

        lLMessage.setOnClickListener((view) -> {
            view.setVisibility(View.GONE);
            txtTitle.setText("");
            txtBody.setText("");
            if (hasNotShownMessageInfo()) {
                showToast(getString(R.string.webcam_message_info));
                setHasShownMessageInfo();
            }
        });

        setTitleToCameraName();

        // TODO Les Menuires: https://lesmenuires.com/webcams/
        // TODO Meribel: https://ski-resort.meribel.net/all-the-webcam.html

        focused = false;

        imgWebcam.setOnClickListener(view -> {
            if (!focused) {
                imgDownload.setVisibility(View.GONE);
                txtDate.setVisibility(View.GONE);
                txtWebCamTitle.setVisibility(View.GONE);
                lLMessage.setVisibility(View.GONE);
            } else {
                if (showDownload) {
                    imgDownload.setVisibility(View.VISIBLE);
                }
                txtDate.setVisibility(View.VISIBLE);
                txtWebCamTitle.setVisibility(View.VISIBLE);
                if (showMessages && (!txtBody.getText().toString().isEmpty() || !txtTitle.getText().toString().isEmpty())) {
                    lLMessage.setVisibility(View.VISIBLE);
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

    /*private boolean isFirstLaunch() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(AboutActivity.PREFS_FIRST_LAUNCH_KEY, true);
    }

    private void setFirstLaunch() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(AboutActivity.PREFS_FIRST_LAUNCH_KEY, false).apply();
    }*/

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void setTitleToCameraName() {
        txtWebCamTitle.setText(clickedWebcam.name);
        getImage(clickedWebcam);
    }

    private void getImage(Webcam webcam) {
        if (imageDownloader == null) {
            imageDownloader = new ImageDownloader();
        } else {
            imageDownloader.cancel();
        }
        txtDate.setVisibility(View.INVISIBLE);
        imageDownloader.startDownload(webcam, WebcamActivity.this, this);
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void onDownloadClicked(View view) {
        if (view.getId() == R.id.imgDownload) {
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Explain, the user has previously denied the request
                        showPermissionNeededDialog();
                    } else {
                        // Ignore, user has denied a permission and selected the Don't ask again option in the permission request dialog, or if a device policy prohibits the permission
                        askForPermission();
                    }
                }
            } else {
                downloadImage();
            }*/
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
        //request.setTitle(getString(R.string.webcam_download_notification_title, clickedWebcam.name));
        request.setDescription(txtDate.getText().toString());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = clickedWebcam.name
                + "-"
                + (imageDate == null ? System.currentTimeMillis() : imageDate)
                + ".jpg";
        fileName = fileName.replace(" ", "_").replace(":","");
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
            imgDownload.setVisibility(View.GONE);
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
                imgDownload.setVisibility(View.VISIBLE);
            }
            currentURL = currentURL.replace("http:", "https:");
            this.imageUrl = currentURL;
            if (imageDate != null && imageDate.startsWith("Taken at")) {
                this.imageDate = imageDate.split("Taken at ")[1];
            }

            Log.e(TAG, "onImageInfoFound: " + currentURL + "; date: " + imageDate);

            asyncTask = new DownloadFileFromURL(this, currentURL, this).execute();
            imgWebcam.setVisibility(View.VISIBLE);
            txtDate.setText(imageDate);
            txtDate.setVisibility(View.VISIBLE);

            if (showMessages) {
                boolean showTitle = false, showBody = false;
                if (!title.isEmpty()) {
                    txtTitle.setText(title);
                    showTitle = true;
                }
                if (!body.isEmpty()) {
                    txtBody.setText(body);
                    showBody = true;
                }
                if (showTitle || showBody) {
                    lLMessage.setVisibility(View.VISIBLE);
                    if (!showTitle) {
                        txtTitle.setVisibility(View.GONE);
                    }
                    if (!showBody) {
                        txtBody.setVisibility(View.GONE);
                    }
                } else {
                    lLMessage.setVisibility(View.GONE);
                }
            }
            /*Picasso.get()
                    .load(currentURL)
                    .resize(0, height)
                    .centerInside()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imgWebcam, new Callback() {
                        @Override
                        public void onSuccess() {
                            WebcamActivity.this.loadingPanel.setVisibility(View.GONE);
                            imgWebcam.setVisibility(View.VISIBLE);
                            txtDate.setText(imageDate);
                            txtDate.setVisibility(View.VISIBLE);

                            if (showMessages) {
                                boolean showTitle = false, showBody = false;
                                if (!title.isEmpty()) {
                                    txtTitle.setText(title);
                                    showTitle = true;
                                }
                                if (!body.isEmpty()) {
                                    txtBody.setText(body);
                                    showBody = true;
                                }
                                if (showTitle || showBody) {
                                    lLMessage.setVisibility(View.VISIBLE);
                                    if (!showTitle) {
                                        txtTitle.setVisibility(View.GONE);
                                    }
                                    if (!showBody) {
                                        txtBody.setVisibility(View.GONE);
                                    }
                                } else {
                                    lLMessage.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (!isFinishing() && !isDestroyed()) {
                                    showErrorDialog(errorMessage);
                                }
                            } else {
                                if (!isFinishing()) {
                                    showErrorDialog(errorMessage);
                                }
                            }
                        }
                    });*/
        }
    }

    @Override
    public void onImageProgress(int progress, long total, long lengthOfFile) {
        progressBar.setProgress(progress);
        double d1 = (total + 0.0) / 1000000;
        double max = (lengthOfFile + 0.0) / 1000000;
        runOnUiThread(() -> txtProgress.setText(getString(R.string.webcam_progress, progress, formater.format(d1), formater.format(max))));
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
            imgWebcam.setImage(ImageSource.uri(file.toString()));
            imgWebcam.setMinimumDpi(60);
            loadingPanel.setVisibility(View.GONE);
        } else {
            showErrorDialog(e.getMessage());
        }
    }

    private void showErrorDialog(@Nullable String errorMessage) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        String message = getString(R.string.webcam_load_error);
        if (errorMessage != null) {
            message = message + "Error: " + errorMessage;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isFinishing() || isDestroyed()) {
                return;
            }
        } else {
            if (isFinishing()) {
                return;
            }
        }
        builder.setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showPermissionNeededDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
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
