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
                .load(R.drawable.map)
                .resize(1067,489) // half size
                .into(imgMap);

        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double posX = (int) event.getX();
                    //int posY = (int) event.getY();
                    txtCoordinates.setText("Touch coordinates : " + String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    double maxWidth = imgMap.getWidth();
                    double touchedXPercentage = posX / maxWidth;
                    if (checkConnection()) {
                        if (touchedXPercentage < 0.15) { //Punkt 1
                            MainActivity.clickedImageNumber = 1;
                            startActivity(new Intent(ChooseFromMap.this, Webcam.class));
                            //finish();
                        } else if (touchedXPercentage < 0.33) { //Punkt 2
                            MainActivity.clickedImageNumber = 2;
                            startActivity(new Intent(ChooseFromMap.this, Webcam.class));
                            //finish();
                        } else if (touchedXPercentage < 0.52) { //Punkt 3
                            MainActivity.clickedImageNumber = 3;
                            startActivity(new Intent(ChooseFromMap.this, Webcam.class));
                            //finish();
                        } else { //Punkt 4
                            MainActivity.clickedImageNumber = 4;
                            startActivity(new Intent(ChooseFromMap.this, Webcam.class));
                            //finish();
                        }
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
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

}


/*
MAX X: 2390

PUNKT 1 X: 142
    MITTEN = 590+142 / 2 ==== 366 == 0.15%
PUNKT 2 X: 590
    MITTEN = 590 + 1000 ==== 795 == 0.33%
PUNKT 3 X: 1000
    MITTEN = 1000 + 1510 ==== 1255 == 0.52%
PUNKT 4 X: 1510
 */