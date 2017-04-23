package se.swecookie.valthorens;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //Från vänster till höger
    //public static String funitel_3_vallees = "http://www.valthorens.com/en/live/livecams--webcams/Webcam-funitel-3-vallees.792.html";
    //public static String folie_douce_plein_sud = "http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html"; //STATISK LÄNK
    //public static String de_la_maison = "http://www.valthorens.com/en/live/livecams--webcams/Webcam-de-la-maison.794.html";
    //public static String livecam_360 = "http://www.valthorens.com/en/live/livecams--webcams/resort-livecam.550.html"; //Svår URL
    //public static String les_2_lacs = "http://www.valthorens.com/en/live/livecams--webcams/Webcam-les-2-lacs.726.html";
    //public static String funitel_de_thorens = "http://www.valthorens.com/en/live/livecams--webcams/Webcam-funitel-de-thorens.784.html";
    //public static String cime_caron = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg"; //Funkar typ inte, massa rutor
    //Finns 2 till (typ statiska), La Tyrolienne & Plan Bouchet

    public static int clickedImageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        boolean connected = checkConnection();
        switch (view.getId()) {
            case R.id.choose_from_map:
                startActivity(new Intent(MainActivity.this, ChooseFromMap.class));
                break;
            case R.id.funitel_3_vallees:
                if (connected) {
                    clickedImageNumber = 1;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.de_la_maison:
                if (connected) {
                    clickedImageNumber = 2;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.les_2_lacs:
                if (connected) {
                    clickedImageNumber = 3;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.funitel_de_thorens:
                if (connected) {
                    clickedImageNumber = 4;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.la_tyrolienne:
                if (connected) {
                    clickedImageNumber = 5;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.plan_bouchet:
                if (connected) {
                    clickedImageNumber = 6;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.livecam_360:
                if (connected) {
                    clickedImageNumber = 7;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
            case R.id.plein_sud:
                if (connected) {
                    clickedImageNumber = 8;
                    startActivity(new Intent(MainActivity.this, Webcam.class));
                } else {
                    showConnectionError();
                }
                break;
        }
    }

    private void showConnectionError() {
        new AlertDialog.Builder(MainActivity.this)
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

    public void onAboutClicked(View view){
        startActivity(new Intent(MainActivity.this, About.class));
    }

}
