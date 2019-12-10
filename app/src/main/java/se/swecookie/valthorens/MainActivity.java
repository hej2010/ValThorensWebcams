package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final Webcam[] webcams = Webcam.values();
    private LinearLayout llTitle, llAbout;
    private RecyclerView mRecyclerView;
    private List<Preview> previews;
    private RecyclerView.Adapter mAdapter;

    private boolean titleShown, aboutShown;
    private int nrOfItemsPerRow;
    public static boolean showPreviews = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.recyclerView);
        llTitle = findViewById(R.id.llTitle);
        llAbout = findViewById(R.id.llAbout);
        llAbout.setVisibility(View.GONE);
        titleShown = true;
        aboutShown = false;
        checkLoadPreviewsSettings();

        previews = new ArrayList<>(12);
        for (Webcam w : webcams) {
            previews.add(new Preview(w));
        }

        recalculateScreen();
    }

    private void checkLoadPreviewsSettings() {
        showPreviews = getSharedPreferences(AboutActivity.PREFS_NAME, Context.MODE_PRIVATE).getBoolean(AboutActivity.PREFS_PREVIEWS_KEY, true);
    }

    private static void showConnectionError(final Context context) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        if (context instanceof AppCompatActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (((AppCompatActivity) context).isFinishing() || ((AppCompatActivity) context).isDestroyed()) {
                    return;
                }
            } else {
                if (((AppCompatActivity) context).isFinishing()) {
                    return;
                }
            }
        }
        builder.setTitle(context.getString(R.string.connection_title))
                .setMessage(context.getString(R.string.connection_message))
                .setPositiveButton(context.getString(R.string.ok), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    static boolean checkConnection(final Context context, boolean show) {
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
        if (!connected && show) {
            showConnectionError(context);
        }
        return connected;
    }

    public void onAboutClicked(@SuppressWarnings("unused") View view) {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recalculateScreen();
    }

    private void recalculateScreen() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;

        float limit = (getResources().getDimension(R.dimen.recyclerWidth) / displayMetrics.density) * 2;

        if (width > limit && nrOfItemsPerRow != 2) {
            nrOfItemsPerRow = 2;
        } else if (width <= limit && nrOfItemsPerRow != 1) {
            nrOfItemsPerRow = 1;
        } else {
            return;
        }

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (nrOfItemsPerRow == 1) {
            mAdapter = new MainAdapter(this, previews);
        } else {
            mAdapter = new MainAdapterDouble(this, previews);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                updateViews(recyclerView);
            }
        });
        updateViews(mRecyclerView);
        mAdapter.notifyItemRangeChanged(0, webcams.length);
    }

    private void updateViews(@NonNull final RecyclerView recyclerView) {
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
        if (!aboutShown && !recyclerView.canScrollVertically(1) &&
                ((nrOfItemsPerRow == 1 && pos == Webcam.NR_OF_WEBCAMS) || (nrOfItemsPerRow == 2 && pos == Webcam.NR_OF_WEBCAMS / 2))) {
            aboutShown = true;
            llAbout.setVisibility(View.VISIBLE);
            recyclerView.post(() -> recyclerView.smoothScrollBy(0, 400, new LinearInterpolator()));
        } else if (aboutShown && ((nrOfItemsPerRow == 1 && pos < Webcam.NR_OF_WEBCAMS) || (nrOfItemsPerRow == 2 && pos < Webcam.NR_OF_WEBCAMS / 2))) {
            aboutShown = false;
            llAbout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoadPreviewsSettings();
        if (mAdapter != null) {
            mAdapter.notifyItemRangeChanged(0, webcams.length);
        }
    }
}
