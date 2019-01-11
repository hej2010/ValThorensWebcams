package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    public static Webcam clickedWebcam = Webcam.FUNITEL_DE_THORENS;
    public static Webcam[] webcams;
    private LinearLayout llTitle, llAbout;
    private boolean titleShown, aboutShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        if (webcams == null) {
            webcams = Webcam.values();
        }

        final RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        llTitle = findViewById(R.id.llTitle);
        llAbout = findViewById(R.id.llAbout);
        llAbout.setVisibility(View.GONE);
        titleShown = true;
        aboutShown = false;
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new MainAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int pos = layoutManager.findFirstVisibleItemPosition();
                if (!titleShown && !recyclerView.canScrollVertically(-1) && pos == 0) {
                    titleShown = true;
                    llTitle.setVisibility(View.VISIBLE);
                } else if (titleShown && pos > 0) {
                    titleShown = false;
                    llTitle.setVisibility(View.GONE);
                }
                pos = layoutManager.findLastVisibleItemPosition();
                if (!aboutShown && !recyclerView.canScrollVertically(1) && pos == Webcam.NR_OF_WEBCAMS) {
                    aboutShown = true;
                    llAbout.setVisibility(View.VISIBLE);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollBy(0, 400, new LinearInterpolator());
                        }
                    });
                } else if (aboutShown && pos < Webcam.NR_OF_WEBCAMS) {
                    aboutShown = false;
                    llAbout.setVisibility(View.GONE);
                }
            }
        });
    }

    private static void showConnectionError(final Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(context.getString(R.string.connection_title))
                .setMessage(context.getString(R.string.connection_message))
                .setPositiveButton(context.getString(R.string.ok), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    static boolean checkConnection(final Context context) {
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
        if (!connected) {
            showConnectionError(context);
        }
        return connected;
    }

    public void onAboutClicked(View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

}
