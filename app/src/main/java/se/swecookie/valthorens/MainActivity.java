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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import se.swecookie.valthorens.helper.Links;

public class MainActivity extends AppCompatActivity {
    public static Webcam clickedImageNumber = Webcam.FUNITEL_DE_THORENS;

    private ImageView funitel_3_vallees, de_la_maison, les_2_lacs, funitel_de_thorens, la_tyrolienne, plan_bouchet, livecam_360, plein_sud, tsd_moutiere, cime_caron;
    private ProgressBar progressBar;

    private int loadedCount;
    private static final int TOTAL_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getImages();
    }

    // TODO App-id: ca-app-pub-2831297200743176~1134333565
    // TODO Ad-unit-id: ca-app-pub-2831297200743176/9525504068

    private void init() {
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        funitel_3_vallees = findViewById(R.id.funitel_3_vallees);
        de_la_maison = findViewById(R.id.de_la_maison);
        les_2_lacs = findViewById(R.id.les_2_lacs);
        funitel_de_thorens = findViewById(R.id.funitel_de_thorens);
        la_tyrolienne = findViewById(R.id.la_tyrolienne);
        plan_bouchet = findViewById(R.id.plan_bouchet);
        livecam_360 = findViewById(R.id.livecam_360);
        plein_sud = findViewById(R.id.plein_sud);
        tsd_moutiere = findViewById(R.id.tsd_moutiere);
        cime_caron = findViewById(R.id.cime_caron);
        progressBar = findViewById(R.id.progressBar);
        loadedCount = 0;
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

    private void getImages() {
        Picasso.with(this)
                .load(Links.FUNITEL_3_VALLEES)
                .into(funitel_3_vallees, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.DE_LA_MAISON)
                .into(de_la_maison, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.LES_2_LACS)
                .into(les_2_lacs, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.FUNITEL_DE_THORENS)
                .into(funitel_de_thorens, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.LA_TYROLIENNE)
                .into(la_tyrolienne, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.PLAN_BOUCHET)
                .into(plan_bouchet, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.LIVECAM_360)
                .into(livecam_360, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.PLEIN_SUD)
                .into(plein_sud, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.TSD_MOUTIERE)
                .into(tsd_moutiere, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
        Picasso.with(this)
                .load(Links.CIME_CARON)
                .into(cime_caron, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadedCount++;
                        if (loadedCount >= TOTAL_COUNT) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
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

    public void onAboutClicked(View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

}
