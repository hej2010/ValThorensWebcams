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
    private Webcam clickedWebcam;

    private static final String prefsFirstLaunch = "firstLaunch";
    public static final String EXTRA_WEBCAM = "w";

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcam2);

        txtWebCamTitle = findViewById(R.id.txtWebCamTitle);
        imgWebcam = findViewById(R.id.imgWebcam);
        txtDate = findViewById(R.id.txtDate);
        loadingPanel = findViewById(R.id.loadingPanel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedWebcam = (Webcam) extras.getSerializable(EXTRA_WEBCAM);
        } else {
            throw new IllegalStateException("clickedWebcam is null!");
        }

        setTitleToCameraName();

        if (isFirstLaunch()) {
            snackbar = Snackbar.make(txtDate, getString(R.string.webcam_fullscreen_hint), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.dismiss), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setFirstLaunch();
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
                    setFirstLaunch();
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

    private boolean isFirstLaunch() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WebcamActivity.this);
        return prefs.getBoolean(prefsFirstLaunch, true);
    }

    private void setFirstLaunch() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WebcamActivity.this);
        prefs.edit().putBoolean(prefsFirstLaunch, false).apply();
    }

    private void toggleFullscreen(AppCompatActivity activity) {
        int newUiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void setTitleToCameraName() {
        String s = "";
        String t = "";
        switch (clickedWebcam) {
            case FUNITEL_3_VALLEES:
                t = getString(R.string.camera_1_funitel_3_vallees);
                s = "http://skaping.com/valthorens/3vallees";
                break;
            case DE_LA_MAISON:
                t = getString(R.string.camera_2_de_la_maison);
                s = "http://skaping.com/valthorens/lamaison";
                break;
            case LES_2_LACS:
                t = getString(R.string.camera_3_les_2_lacs);
                s = "http://skaping.com/valthorens/2lacs";
                break;
            case FUNITEL_DE_THORENS:
                t = getString(R.string.camera_4_funitel_de_thorens);
                s = "http://skaping.com/valthorens/funitelthorens";
                break;
            case STADE:
                t = getString(R.string.camera_5_stade);
                s = "http://www.skaping.com/valthorens/stade";
                break;
            case BOISMINT:
                t = getString(R.string.camera_11_boismint);
                s = "http://www.skaping.com/valthorens/boismint";
                break;
            case LA_TYROLIENNE:
                t = getString(R.string.camera_6_la_tyrolienne);
                s = "http://www.valthorens.com/en/webcam/livecam-tyrolienne"; // http://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html
                break;
            case PLAN_BOUCHET:
                t = getString(R.string.camera_7_plan_bouchet);
                s = "http://www.valthorens.com/en/webcam/livecam-plan-bouchet"; // http://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html
                break;
            case LIVECAM_360:
                t = getString(R.string.camera_8_livecam_360);
                s = "http://www.valthorens.com/en/webcam/livecam-station"; // http://www.valthorens.com/en/live/livecams--webcams/resort-livecam.550.html
                break;
            case PLEIN_SUD:
                t = getString(R.string.camera_9_plein_sud);
                s = "http://www.valthorens.com/en/webcam/livecam-la-folie-douce-plein-sud"; // http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html
                break;
            case CIME_CARON:
                t = getString(R.string.camera_10_cime_caron);
                s = "http://www.valthorens.com/en/webcam/livecam-cime-caron"; // http://www.valthorens.com/en/live/livecams--webcams/webcam-cime-caron.416.html
                break;
        }
        txtWebCamTitle.setText(t);
        getImage(clickedWebcam.i, s);
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
        if (imageDownloader != null) {
            imageDownloader.cancel();
        }
    }
}
