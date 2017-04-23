package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ChooseFromMap extends AppCompatActivity {
    private ImageView imgMap;
    private TextView txtCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_map);

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);

        Picasso.with(ChooseFromMap.this)
                .load(R.drawable.map3)
                .resize(1067, 489) // half size
                .into(imgMap);

        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double posX = event.getX();
                    txtCoordinates.setText("Touch coordinates : " + posX + "x" + event.getY());
                    double maxWidth = imgMap.getWidth();
                    double touchedXPercentage = posX / maxWidth;
                    if (checkConnection()) {
                        if (touchedXPercentage < 0.1102) { //Funitel 3 vallees
                            MainActivity.clickedImageNumber = 1;
                        } else if (touchedXPercentage < 0.2) { //Plein sud
                            MainActivity.clickedImageNumber = 8;
                        } else if (touchedXPercentage < 0.276) { //De la maison
                            MainActivity.clickedImageNumber = 2;
                        } else if (touchedXPercentage < 0.3625) { //Livecam 360
                            MainActivity.clickedImageNumber = 7;
                        } else if (touchedXPercentage < 0.54) { //Les 2 lacs
                            MainActivity.clickedImageNumber = 3;
                        } else if (touchedXPercentage < 0.693) { //Funitel de thorens
                            MainActivity.clickedImageNumber = 4;
                        } else if (touchedXPercentage < 0.83) { //La tryolienne
                            MainActivity.clickedImageNumber = 5;
                        } else {    //Plan Bouchet
                            MainActivity.clickedImageNumber = 6;
                        }
                        startActivity(new Intent(ChooseFromMap.this, Webcam.class));
                    } else {
                        showConnectionError();
                    }
                }
                return true;
            }
        });
    }

    private void showConnectionError() {
        new AlertDialog.Builder(ChooseFromMap.this)
                .setTitle("Connection error")
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
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
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