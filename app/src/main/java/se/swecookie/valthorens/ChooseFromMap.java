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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ChooseFromMap extends AppCompatActivity {
    private ImageView imgMap;
    private TextView txtCoordinates;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_from_map);

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        btn8 = (Button) findViewById(R.id.btn8);

        Picasso.with(ChooseFromMap.this)
                .load(R.drawable.map3)
                .resize(1067, 489) // half size
                .into(imgMap);

        setButtonCoordinates();

        imgMap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double posX = (int) event.getX();
                    //int posY = (int) event.getY();
                    txtCoordinates.setText("Touch coordinates : " + String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    double maxWidth = imgMap.getWidth();
                    double touchedXPercentage = posX / maxWidth;
                    /*if (checkConnection()) {
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
                    }*/
                }
                return true;
            }
        });
    }

    private void setButtonCoordinates() {
        btn1.setX(55);
        btn1.setY(615);

        btn2.setX(295);
        btn2.setY(615);

        btn3.setX(499);
        btn3.setY(727);

        btn4.setX(655);
        btn4.setY(720);

        btn5.setX(920);
        btn5.setY(727);

        btn6.setX(1430);
        btn6.setY(320);

        btn7.setX(1720);
        btn7.setY(160);

        btn8.setX(2120);
        btn8.setY(710);
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

 */