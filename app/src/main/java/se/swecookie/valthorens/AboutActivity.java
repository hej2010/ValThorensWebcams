package se.swecookie.valthorens;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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
