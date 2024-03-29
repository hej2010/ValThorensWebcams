package se.swecookie.valthorens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import se.swecookie.valthorens.data.Webcam;

public class ChooseFromMapActivity extends AppCompatActivity {
    private ImageView imgMap;

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_map);

        imgMap = findViewById(R.id.imgMap);
        //final TextView txtCoordinates = findViewById(R.id.txtCoordinates);
        //txtCoordinates.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(R.drawable.map6)
                //.resize(1067, 489) // half size
                .fit()
                .centerInside()
                .into(imgMap);

        imgMap.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                double posX = event.getX();
                double posY = event.getY();
                double maxWidth = imgMap.getWidth();
                double maxHeight = imgMap.getHeight();
                double touchedXPercentage = posX / maxWidth;
                double touchedYPercentage = posY / maxHeight;
                //txtCoordinates.setText("Touch coordinates : " + posX + "x" + event.getY() + ", perc: " + (int) (touchedXPercentage * 1000) + ", " + (int) (touchedYPercentage * 1000));
                if (MainActivity.checkConnection(ChooseFromMapActivity.this, true)) {
                    Webcam clickedWebcam;
                    if (touchedXPercentage < 0.1102) { //Funitel 3 vallees
                        clickedWebcam = Webcam.FUNITEL_3_VALLEES;
                    } else if (touchedXPercentage < 0.2) { //Plein sud
                        clickedWebcam = Webcam.PLEIN_SUD;
                    } else if (touchedXPercentage < 0.276) { //De la maison
                        if (touchedYPercentage > 0.5) {
                            clickedWebcam = Webcam.DE_LA_MAISON;
                        } else {
                            clickedWebcam = Webcam.STADE;
                        }
                    } else if (touchedXPercentage < 0.3625) { //Livecam 360
                        clickedWebcam = Webcam.LIVECAM_360;
                    } else if (touchedXPercentage < 0.46) { //Les 2 lacs
                        clickedWebcam = Webcam.LES_2_LACS;
                    } else if (touchedXPercentage < 0.58) { //Boismint
                        clickedWebcam = Webcam.BOISMINT;
                    } else if (touchedXPercentage < 0.693) { //Funitel de thorens
                        clickedWebcam = Webcam.FUNITEL_DE_THORENS;
                    } else if (touchedXPercentage < 0.83 && touchedYPercentage < 0.36) { //La tryolienne
                        clickedWebcam = Webcam.LA_TYROLIENNE;
                    } else if (touchedXPercentage < 0.83 && touchedYPercentage > 0.36) { // Cime Caron
                        clickedWebcam = Webcam.CIME_CARON;
                    } else {    //Plan Bouchet
                        clickedWebcam = Webcam.PLAN_BOUCHET;
                    }
                    startActivity(new Intent(ChooseFromMapActivity.this, WebcamActivity.class)
                            .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
                }
            }
            return true;
        });
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

// --Commented out by Inspection START (2019-12-10 16:37):
//    int getWidth() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        return displayMetrics.widthPixels;
//    }
// --Commented out by Inspection STOP (2019-12-10 16:37)

}