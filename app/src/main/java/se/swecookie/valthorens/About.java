package se.swecookie.valthorens;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onReturnClicked(View view) {
        finish();
    }

    public void onImageClicked(View view) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.imageView), getString(R.string.about_image), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
