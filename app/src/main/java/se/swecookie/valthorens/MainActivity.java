package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    public static Webcam clickedImageNumber = Webcam.FUNITEL_DE_THORENS;
    private ProgressBar progressBar;

    private int loadedCount;
    private static final int TOTAL_COUNT = 11;
    private static final int[] WEBCAM_DRAWABLE_ID = {R.drawable.funitel_3_vallees, R.drawable.de_la_maison, R.drawable.les_2_lacs, R.drawable.funitel_de_thorens, R.drawable.la_tyrolienne,
            R.drawable.stade, R.drawable.plan_bouchet, R.drawable.boismint, R.drawable.livecam_360, R.drawable.plein_sud, R.drawable.cime_caron};
    private ImageView[] WEBCAM_IMAGEVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getImages();
    }

    private void init() {
        ImageView funitel_3_vallees = findViewById(R.id.funitel_3_vallees);
        ImageView de_la_maison = findViewById(R.id.de_la_maison);
        ImageView les_2_lacs = findViewById(R.id.les_2_lacs);
        ImageView funitel_de_thorens = findViewById(R.id.funitel_de_thorens);
        ImageView la_tyrolienne = findViewById(R.id.la_tyrolienne);
        ImageView stade = findViewById(R.id.stade);
        ImageView boismint = findViewById(R.id.boismint);
        ImageView plan_bouchet = findViewById(R.id.plan_bouchet);
        ImageView livecam_360 = findViewById(R.id.livecam_360);
        ImageView plein_sud = findViewById(R.id.plein_sud);
        ImageView cime_caron = findViewById(R.id.cime_caron);
        progressBar = findViewById(R.id.progressBar);
        loadedCount = 0;

        WEBCAM_IMAGEVIEW = new ImageView[]{funitel_3_vallees, de_la_maison, les_2_lacs, funitel_de_thorens, la_tyrolienne, stade, plan_bouchet, boismint, livecam_360, plein_sud, cime_caron};
    }

    public void onClick(View view) {
        boolean connected = checkConnection(MainActivity.this);
        if (!connected && view.getId() != R.id.choose_from_map) {
            showConnectionError(this);
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
                case R.id.stade:
                    clickedImageNumber = Webcam.STADE;
                    break;
                case R.id.boismint:
                    clickedImageNumber = Webcam.BOISMINT;
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
                case R.id.cime_caron:
                    clickedImageNumber = Webcam.CIME_CARON;
                    break;
            }
            startActivity(new Intent(MainActivity.this, WebcamActivity.class));
        }
    }

    static void showConnectionError(final AppCompatActivity context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(context.getString(R.string.connection_title))
                .setMessage(context.getString(R.string.connection_message))
                .setPositiveButton(context.getString(R.string.open_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                })
                .setNegativeButton(context.getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void getImages() {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            Picasso.get()
                    .load(WEBCAM_DRAWABLE_ID[i])
                    .into(WEBCAM_IMAGEVIEW[i], new Callback() {
                        @Override
                        public void onSuccess() {
                            loadedCount++;
                            if (loadedCount >= TOTAL_COUNT) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    static boolean checkConnection(AppCompatActivity context) {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        }
        return connected;
    }

    public void onAboutClicked(View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

}
