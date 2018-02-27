package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ChooseFromMapActivity extends AppCompatActivity {
    private ImageView imgMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_map);

        imgMap = findViewById(R.id.imgMap);
        //final TextView txtCoordinates = findViewById(R.id.txtCoordinates);

        Picasso.with(ChooseFromMapActivity.this)
                .load(R.drawable.map5)
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
                    if (checkConnection()) {
                        if (touchedXPercentage < 0.1102) { //Funitel 3 vallees
                            MainActivity.clickedImageNumber = Webcam.FUNITEL_3_VALLEES;
                        } else if (touchedXPercentage < 0.2) { //Plein sud
                            MainActivity.clickedImageNumber = Webcam.PLEIN_SUD;
                        } else if (touchedXPercentage < 0.276) { //De la maison
                            if (touchedYPercentage > 0.5) {
                                MainActivity.clickedImageNumber = Webcam.DE_LA_MAISON;
                            } else {
                                MainActivity.clickedImageNumber = Webcam.STADE;
                            }
                        } else if (touchedXPercentage < 0.3625) { //Livecam 360
                            MainActivity.clickedImageNumber = Webcam.LIVECAM_360;
                        } else if (touchedXPercentage < 0.46) { //Les 2 lacs
                            MainActivity.clickedImageNumber = Webcam.LES_2_LACS;
                        } else if (touchedXPercentage < 0.693) { //Funitel de thorens
                            MainActivity.clickedImageNumber = Webcam.FUNITEL_DE_THORENS;
                        } else if (touchedXPercentage < 0.83 && touchedYPercentage < 0.36) { //La tryolienne
                            MainActivity.clickedImageNumber = Webcam.LA_TYROLIENNE;
                        } else if (touchedXPercentage < 0.83 && touchedYPercentage > 0.36) { // Cime Caron
                            MainActivity.clickedImageNumber = Webcam.CIME_CARON;
                        } else {    //Plan Bouchet
                            MainActivity.clickedImageNumber = Webcam.PLAN_BOUCHET;
                        }
                        startActivity(new Intent(ChooseFromMapActivity.this, WebcamActivity.class));
                    } else {
                        showConnectionError();
                    }
                }
                return true;
            }
        });
    }

    private void showConnectionError() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Connection error")
                .setMessage("You need an active internet connection to view a webcam!")
                .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean checkConnection() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                connected = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                connected = true;
            }
        } else {
            // not connected to the internet
            connected = false;
        }
        return connected;
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