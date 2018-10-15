package se.swecookie.valthorens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebcamActivity extends AppCompatActivity {
    private TextView txtWebCamTitle, txtDate;
    private ImageView imgWebcam;
    private RelativeLayout loadingPanel;
    private ImageDownloader imageDownloader;

    private boolean focused;
    private Snackbar snackbar = null;

    private static final String prefsFirstLaunch = "firstLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcam2);

        txtWebCamTitle = findViewById(R.id.txtWebCamTitle);
        imgWebcam = findViewById(R.id.imgWebcam);
        txtDate = findViewById(R.id.txtDate);
        loadingPanel = findViewById(R.id.loadingPanel);

        setTitleToCameraName();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WebcamActivity.this);
        boolean isFirstLaunch = prefs.getBoolean(prefsFirstLaunch, true);

        if (isFirstLaunch) {
            snackbar = Snackbar.make(imgWebcam, getString(R.string.webcam_fullscreen_hint), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            prefs.edit().putBoolean(prefsFirstLaunch, false).apply();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.colorTextLight));
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(WebcamActivity.this, R.color.colorAccent));
            snackbar.show();
        }

        focused = false;

        imgWebcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                    prefs.edit().putBoolean(prefsFirstLaunch, false).apply();
                }
                if (!focused) {
                    txtDate.setVisibility(View.GONE);
                    txtWebCamTitle.setVisibility(View.GONE);
                } else {
                    txtDate.setVisibility(View.VISIBLE);
                    txtWebCamTitle.setVisibility(View.VISIBLE);
                }
                focused = !focused;
                toggleFullscreen(WebcamActivity.this);
            }
        });
    }

    private void toggleFullscreen(AppCompatActivity activity) {
        int newUiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void setTitleToCameraName() {
        switch (MainActivity.clickedImageNumber) {
            case FUNITEL_3_VALLEES:
                txtWebCamTitle.setText(getString(R.string.camera_1_funitel_3_vallees));
                getImage(1, "http://skaping.com/valthorens/3vallees");
                break;
            case DE_LA_MAISON:
                txtWebCamTitle.setText(getString(R.string.camera_2_de_la_maison));
                getImage(2, "http://skaping.com/valthorens/lamaison");
                break;
            case LES_2_LACS:
                txtWebCamTitle.setText(getString(R.string.camera_3_les_2_lacs));
                getImage(3, "http://skaping.com/valthorens/2lacs");
                break;
            case FUNITEL_DE_THORENS:
                txtWebCamTitle.setText(getString(R.string.camera_4_funitel_de_thorens));
                getImage(4, "http://skaping.com/valthorens/funitelthorens");
                break;
            case STADE:
                txtWebCamTitle.setText(getString(R.string.camera_5_stade));
                getImage(5, "http://www.skaping.com/valthorens/stade");
                break;
            case LA_TYROLIENNE:
                txtWebCamTitle.setText(getString(R.string.camera_6_la_tyrolienne));
                getImage(6, "http://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html");
                break;
            case PLAN_BOUCHET:
                txtWebCamTitle.setText(getString(R.string.camera_7_plan_bouchet));
                getImage(7, "http://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html");
                break;
            case LIVECAM_360:
                txtWebCamTitle.setText(getString(R.string.camera_8_livecam_360));
                getImage(8, "http://www.valthorens.com/en/live/livecams--webcams/resort-livecam.550.html");
                break;
            case PLEIN_SUD:
                txtWebCamTitle.setText(getString(R.string.camera_9_plein_sud));
                getImage(9, "http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html");
                break;
            case CIME_CARON:
                txtWebCamTitle.setText(getString(R.string.camera_10_cime_caron));
                getImage(10, "http://www.valthorens.com/en/live/livecams--webcams/webcam-cime-caron.416.html");
                break;
        }
    }

    private void getImage(int id, String url) {
        if (imageDownloader == null) {
            imageDownloader = new ImageDownloader();
        } else {
            imageDownloader.cancel();
        }
        imageDownloader.startDownload(imgWebcam, txtDate, id, url, WebcamActivity.this, loadingPanel);
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageDownloader.cancel();
    }
}
