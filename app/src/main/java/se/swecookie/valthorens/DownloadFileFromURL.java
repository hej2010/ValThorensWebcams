package se.swecookie.valthorens;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileFromURL extends AsyncTask<Void, Void, Pair<Uri, Exception>> {
    private final String url;
    private final IOnImageDownloaded onImageDownloaded;
    private final WeakReference<AppCompatActivity> weakReference;

    DownloadFileFromURL(AppCompatActivity context, String url, IOnImageDownloaded onImageDownloaded) {
        this.url = url;
        this.onImageDownloaded = onImageDownloaded;
        this.weakReference = new WeakReference<>(context);
    }

    @Override
    protected Pair<Uri, Exception> doInBackground(Void... voids) {
        int count;
        OutputStream output = null;
        InputStream input = null;
        AppCompatActivity context = weakReference.get();
        Exception e = null;
        try {
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            connection.connect();

            input = new BufferedInputStream(url.openStream(), 8192);
            Log.e("TAG", "doInBackground: " + context.getCacheDir().toString());
            File outputFile = new File(context.getCacheDir(), System.currentTimeMillis() + "");
            output = new FileOutputStream(outputFile);
            Log.e("TAG", "doInBackground: save to " + outputFile.toString());

            long lengthOfFile = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                lengthOfFile = connection.getContentLengthLong();
            } else {
                lengthOfFile = connection.getContentLength();
            }
            long total = 0;
            byte[] data = new byte[1024];
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    return null;
                }
                total += count;
                onImageDownloaded.onImageProgress((int) ((total * 100) / lengthOfFile), total, lengthOfFile);
                output.write(data, 0, count);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (context.isFinishing() || context.isDestroyed()) {
                    return null;
                }
            } else {
                if (context.isFinishing()) {
                    return null;
                }
            }

            return new Pair<>(Uri.fromFile(outputFile), null);
        } catch (Exception e2) {
            e = e2;
            e2.printStackTrace();
            Log.e("Error: ", e2.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.flush();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        return new Pair<>(null, e);
    }

    @Override
    protected void onPostExecute(Pair<Uri, Exception> pair) {
        AppCompatActivity context = weakReference.get();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context.isFinishing() || context.isDestroyed()) {
                return;
            }
        } else {
            if (context.isFinishing()) {
                return;
            }
        }

        Uri uri = pair.first;
        if (uri != null) {
            onImageDownloaded.onImageSavedToCache(uri, null);
        } else {
            onImageDownloaded.onImageSavedToCache(null, pair.second);
        }
    }

}