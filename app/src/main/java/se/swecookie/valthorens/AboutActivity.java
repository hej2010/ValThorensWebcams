package se.swecookie.valthorens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class AboutActivity extends AppCompatActivity {
    static String PREFS_NAME = "prefs";
    static String PREFS_MESSAGES_KEY = "messages";
    static String PREFS_PREVIEWS_KEY = "previews";
    static final String PREFS_FIRST_LAUNCH_KEY = "first";
    static final String PREFS_HAS_SHOWN_MESSAGE_INFO_KEY = "info";
    private Toast toast;

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        CheckBox cBMessages = findViewById(R.id.cBMessages);
        CheckBox cBPreviews = findViewById(R.id.cBPreviews);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        cBMessages.setChecked(prefs.getBoolean(PREFS_MESSAGES_KEY, true));
        cBMessages.setOnCheckedChangeListener((compoundButton, isChecked) -> prefs.edit().putBoolean(PREFS_MESSAGES_KEY, isChecked).apply());
        cBPreviews.setChecked(prefs.getBoolean(PREFS_PREVIEWS_KEY, true));
        cBPreviews.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            prefs.edit().putBoolean(PREFS_PREVIEWS_KEY, isChecked).apply();
            toast.cancel();
            toast = Toast.makeText(this, "Restart the app to apply changes", Toast.LENGTH_SHORT);
            toast.show();
        });
    }

    public void onReturnClicked(@NonNull View view) {
        if (view.getId() == R.id.button) {
            finish();
        }
    }

    public void onImageClicked(@NonNull View view) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.imageView), getString(R.string.about_image), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
