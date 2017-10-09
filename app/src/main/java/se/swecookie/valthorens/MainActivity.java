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
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static Webcam clickedImageNumber = Webcam.FUNITEL_DE_THORENS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        boolean connected = checkConnection();
        if (!connected) {
            showConnectionError();
        } else {
            switch (view.getId()) {
                case R.id.choose_from_map:
                    startActivity(new Intent(MainActivity.this, ChooseFromMapActivity.class));
                    return;
                case R.id.funitel_3_vallees:
                    clickedImageNumber = Webcam.FUNITEL_3_VALLEES;
                    break;
                case R.id.de_la_maison:
                    clickedImageNumber = Webcam.DE_LA_MAISON;
                    break;
                case R.id.les_2_lacs:
                    clickedImageNumber = Webcam.LES_2_LACS;
                    break;
                case R.id.funitel_de_thorens:
                    clickedImageNumber = Webcam.FUNITEL_DE_THORENS;
                    break;
                case R.id.la_tyrolienne:
                    clickedImageNumber = Webcam.LA_TYROLIENNE;
                    break;
                case R.id.plan_bouchet:
                    clickedImageNumber = Webcam.PLAN_BOUCHET;
                    break;
                case R.id.livecam_360:
                    clickedImageNumber = Webcam.LIVECAM_360;
                    break;
                case R.id.plein_sud:
                    clickedImageNumber = Webcam.PLEIN_SUD;
                    break;
                case R.id.tsd_moutiere:
                    clickedImageNumber = Webcam.TSD_MOUTIERE;
                    break;
                case R.id.cime_caron:
                    clickedImageNumber = Webcam.CIME_CARON;
                    break;
            }
            startActivity(new Intent(MainActivity.this, WebcamActivity.class));
        }
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

    public void onAboutClicked(View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

}
