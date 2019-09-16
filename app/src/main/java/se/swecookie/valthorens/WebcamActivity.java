package se.swecookie.valthorens;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class WebcamActivity extends AppCompatActivity {
    private TextView txtWebCamTitle, txtDate, txtTitle, txtBody;
    private ImageView imgWebcam;
    private RelativeLayout loadingPanel;
    private ImageDownloader imageDownloader;
    private LinearLayout lLMessage;

    private boolean focused, showMessages;
    private Snackbar snackbar = null;
    private Webcam clickedWebcam;


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
        lLMessage = findViewById(R.id.lLMessage);
        txtTitle = findViewById(R.id.txtTitle);
        txtBody = findViewById(R.id.txtBody);
        lLMessage.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        showMessages = prefs.getBoolean(AboutActivity.PREFS_MESSAGES_KEY, true);

        lLMessage.setOnClickListener((view) -> {
            view.setVisibility(View.GONE);
            txtTitle.setText("");
            txtBody.setText("");
            if (hasNotShownMessageInfo()) {
                Toast.makeText(this, "Disable messages in About", Toast.LENGTH_LONG).show();
                setHasShownMessageInfo();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedWebcam = (Webcam) extras.getSerializable(EXTRA_WEBCAM);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitleToCameraName();

        if (isFirstLaunch()) {
            snackbar = Snackbar.make(txtDate, getString(R.string.webcam_fullscreen_hint), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.dismiss), view -> setFirstLaunch()).setActionTextColor(getResources().getColor(R.color.colorTextLight));
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(WebcamActivity.this, R.color.colorAccent));
            snackbar.show();
        }

        focused = false;

        imgWebcam.setOnClickListener(view -> {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
                setFirstLaunch();
            }
            if (!focused) {
                txtDate.setVisibility(View.GONE);
                txtWebCamTitle.setVisibility(View.GONE);
                lLMessage.setVisibility(View.GONE);
            } else {
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

    private boolean isFirstLaunch() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(AboutActivity.PREFS_FIRST_LAUNCH_KEY, true);
    }

    private void setFirstLaunch() {
        final SharedPreferences prefs = getSharedPreferences(AboutActivity.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(AboutActivity.PREFS_FIRST_LAUNCH_KEY, false).apply();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

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
        imageDownloader.startDownload(imgWebcam, txtDate, id, url, WebcamActivity.this, loadingPanel, txtTitle, txtBody, lLMessage, showMessages);
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
