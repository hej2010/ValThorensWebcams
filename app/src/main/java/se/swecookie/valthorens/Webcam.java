package se.swecookie.valthorens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Webcam extends AppCompatActivity {
    private TextView txtWebCamTitle, txtDate;
    private ImageView imgWebcam;
    private RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcam);

        txtWebCamTitle = (TextView) findViewById(R.id.txtWebCamTitle);
        imgWebcam = (ImageView) findViewById(R.id.imgWebcam);
        txtDate = (TextView) findViewById(R.id.txtDate);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        setTitleToCameraName();
    }

    private void setTitleToCameraName() {
        switch (MainActivity.clickedImageNumber) {
            case 1:
                txtWebCamTitle.setText(getString(R.string.camera_1_funitel_3_vallees));
                getImage(1, "http://www.skaping.com/valthorens/3vallees");
                break;
            case 2:
                txtWebCamTitle.setText(getString(R.string.camera_2_de_la_maison));
                getImage(2, "http://www.skaping.com/valthorens/lamaison");
                break;
            case 3:
                txtWebCamTitle.setText(getString(R.string.camera_3_les_2_lacs));
                getImage(3, "http://www.skaping.com/valthorens/2lacs");
                break;
            case 4:
                txtWebCamTitle.setText(getString(R.string.camera_4_funitel_de_thorens));
                getImage(4, "http://www.skaping.com/valthorens/funitelthorens");
                break;
            case 5:
                txtWebCamTitle.setText(getString(R.string.camera_5_la_tyrolienne));
                getImage(5, "http://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html");
                break;
            case 6:
                txtWebCamTitle.setText(getString(R.string.camera_6_plan_bouchet));
                getImage(6, "http://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html");
                break;
            case 7:
                txtWebCamTitle.setText(getString(R.string.camera_7_livecam_360));
                getImage(7, "http://www.valthorens.com/en/live/livecams--webcams/resort-livecam.550.html");
                break;
            case 8:
                txtWebCamTitle.setText(getString(R.string.camera_8_plein_sud));
                getImage(8, "http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html");
                break;
        }
    }

    private void getImage(int id, String url) {
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.startDownload(imgWebcam, txtDate, id, url, Webcam.this, loadingPanel);
    }

    int getHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
