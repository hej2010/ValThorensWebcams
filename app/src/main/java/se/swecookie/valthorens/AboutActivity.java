package se.swecookie.valthorens;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

    public void onReturnClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                finish();
                break;
        }
    }

    public void onImageClicked(View view) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.imageView), getString(R.string.about_image), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
