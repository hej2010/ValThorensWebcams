package se.swecookie.valthorens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    static final String PREFS_NAME = "prefs";
    static final String PREFS_MESSAGES_KEY = "messages";
    static final String PREFS_PREVIEWS_KEY = "previews";
    static final String PREFS_DOWNLOADS_KEY = "downloads";
    static final String PREFS_HAS_SHOWN_MESSAGE_INFO_KEY = "info";

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        CheckBox cBMessages = findViewById(R.id.cBMessages);
        CheckBox cBPreviews = findViewById(R.id.cBPreviews);
        CheckBox cBDownloads = findViewById(R.id.cBDownloads);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cBMessages.setChecked(prefs.getBoolean(PREFS_MESSAGES_KEY, true));
        cBMessages.setOnCheckedChangeListener((compoundButton, isChecked) -> prefs.edit().putBoolean(PREFS_MESSAGES_KEY, isChecked).apply());
        cBPreviews.setChecked(prefs.getBoolean(PREFS_PREVIEWS_KEY, true));
        cBPreviews.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            prefs.edit().putBoolean(PREFS_PREVIEWS_KEY, isChecked).apply();
            MainActivity.showPreviews = isChecked;
        });
        cBDownloads.setChecked(prefs.getBoolean(PREFS_DOWNLOADS_KEY, false));
        cBDownloads.setOnCheckedChangeListener((compoundButton, isChecked) -> prefs.edit().putBoolean(PREFS_DOWNLOADS_KEY, isChecked).apply());

        ImageView imgHelpMessages = findViewById(R.id.imgHelpMessages);
        imgHelpMessages.setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(AboutActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
            b.setTitle(getString(R.string.about_help_messages_title))
                    .setMessage(getString(R.string.about_help_messages_message))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        });
        ImageView imgHelpPreviews = findViewById(R.id.imgHelpPreviews);
        imgHelpPreviews.setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(AboutActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
            b.setTitle(getString(R.string.about_help_previews_title))
                    .setMessage(getString(R.string.about_help_previews_message))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        });
        ImageView imgHelpDownloads = findViewById(R.id.imgHelpDownloads);
        imgHelpDownloads.setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(AboutActivity.this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
            b.setTitle(getString(R.string.about_help_downloads_title))
                    .setMessage(getString(R.string.about_help_downloads_message))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        });
    }

    public void onReturnClicked(@NonNull View view) {
        if (view.getId() == R.id.button) {
            finish();
        }
    }

}
