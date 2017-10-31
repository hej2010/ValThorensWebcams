package se.swecookie.valthorens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebcamActivity extends AppCompatActivity {
    private TextView txtWebCamTitle, txtDate;
    private ImageView imgWebcam;
    private RelativeLayout loadingPanel;
    private ImageDownloader imageDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcam2);

        txtWebCamTitle = findViewById(R.id.txtWebCamTitle);
        imgWebcam = findViewById(R.id.imgWebcam);
        txtDate = findViewById(R.id.txtDate);
        loadingPanel = findViewById(R.id.loadingPanel);

        setTitleToCameraName();
    }

    private void setTitleToCameraName() { // SÄTT INTE HTTPS PÅ NÅN OM DEN INTE REDIRECTAR EN DIT UTAN HTTPS!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        switch (MainActivity.clickedImageNumber) {
            case FUNITEL_3_VALLEES:
                txtWebCamTitle.setText(getString(R.string.camera_1_funitel_3_vallees));
                getImage(1, "http://skaping.com/valthorens/3vallees"); // INTE HTTPS!!!
                break;
            case DE_LA_MAISON:
                txtWebCamTitle.setText(getString(R.string.camera_2_de_la_maison));
                getImage(2, "http://skaping.com/valthorens/lamaison"); // INTE HTTPS!!!
                break;
            case LES_2_LACS:
                txtWebCamTitle.setText(getString(R.string.camera_3_les_2_lacs));
                getImage(3, "http://skaping.com/valthorens/2lacs"); // INTE HTTPS!!!
                break;
            case FUNITEL_DE_THORENS:
                txtWebCamTitle.setText(getString(R.string.camera_4_funitel_de_thorens));
                getImage(4, "http://skaping.com/valthorens/funitelthorens"); // INTE HTTPS!!!
                break;
            case LA_TYROLIENNE:
                txtWebCamTitle.setText(getString(R.string.camera_5_la_tyrolienne));
                getImage(5, "http://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html"); // INTE HTTPS!!!
                break;
            case PLAN_BOUCHET:
                txtWebCamTitle.setText(getString(R.string.camera_6_plan_bouchet));
                getImage(6, "http://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html"); // INTE HTTPS!!!
                break;
            case LIVECAM_360:
                txtWebCamTitle.setText(getString(R.string.camera_7_livecam_360));
                getImage(7, "http://www.valthorens.com/en/live/livecams--webcams/resort-livecam.550.html"); // INTE HTTPS!!!
                break;
            case PLEIN_SUD:
                txtWebCamTitle.setText(getString(R.string.camera_8_plein_sud));
                getImage(8, "http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html"); // INTE HTTPS!!!
                break;
            case TSD_MOUTIERE:
                txtWebCamTitle.setText(getString(R.string.camera_9_tsd_moutière));
                getImage(9, "http://www.trinum.com/ibox/ftpcam/mega_cime_caron.jpg"); // INTE HTTPS!!!
                break;
            case CIME_CARON:
                txtWebCamTitle.setText(getString(R.string.camera_10_cime_caron));
                getImage(10, "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg"); // INTE HTTPS!!!
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
