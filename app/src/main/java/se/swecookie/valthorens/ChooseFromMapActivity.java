package se.swecookie.valthorens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ChooseFromMapActivity extends AppCompatActivity {
    private ImageView imgMap;

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
                .resize(1067, 489) // half size
                .into(imgMap);

        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double posX = event.getX();
                    double posY = event.getY();
                    double maxWidth = imgMap.getWidth();
                    double maxHeight = imgMap.getHeight();
                    double touchedXPercentage = posX / maxWidth;
                    double touchedYPercentage = posY / maxHeight;
                    //txtCoordinates.setText("Touch coordinates : " + posX + "x" + event.getY() + ", perc: " + (int) (touchedXPercentage * 1000) + ", " + (int) (touchedYPercentage * 1000));
                    if (MainActivity.checkConnection(ChooseFromMapActivity.this)) {
                        if (touchedXPercentage < 0.1102) { //Funitel 3 vallees
                            MainActivity.clickedWebcam = Webcam.FUNITEL_3_VALLEES;
                        } else if (touchedXPercentage < 0.2) { //Plein sud
                            MainActivity.clickedWebcam = Webcam.PLEIN_SUD;
                        } else if (touchedXPercentage < 0.276) { //De la maison
                            if (touchedYPercentage > 0.5) {
                                MainActivity.clickedWebcam = Webcam.DE_LA_MAISON;
                            } else {
                                MainActivity.clickedWebcam = Webcam.STADE;
                            }
                        } else if (touchedXPercentage < 0.3625) { //Livecam 360
                            MainActivity.clickedWebcam = Webcam.LIVECAM_360;
                        } else if (touchedXPercentage < 0.46) { //Les 2 lacs
                            MainActivity.clickedWebcam = Webcam.LES_2_LACS;
                        } else if (touchedXPercentage < 0.58) { //Boismint
                            MainActivity.clickedWebcam = Webcam.BOISMINT;
                        } else if (touchedXPercentage < 0.693) { //Funitel de thorens
                            MainActivity.clickedWebcam = Webcam.FUNITEL_DE_THORENS;
                        } else if (touchedXPercentage < 0.83 && touchedYPercentage < 0.36) { //La tryolienne
                            MainActivity.clickedWebcam = Webcam.LA_TYROLIENNE;
                        } else if (touchedXPercentage < 0.83 && touchedYPercentage > 0.36) { // Cime Caron
                            MainActivity.clickedWebcam = Webcam.CIME_CARON;
                        } else {    //Plan Bouchet
                            MainActivity.clickedWebcam = Webcam.PLAN_BOUCHET;
                        }
                        startActivity(new Intent(ChooseFromMapActivity.this, WebcamActivity.class));
                    }
                }
                return true;
            }
        });
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    int getWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}