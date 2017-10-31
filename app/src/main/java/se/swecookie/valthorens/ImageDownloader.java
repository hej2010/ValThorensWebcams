package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;

class ImageDownloader {
    private ImageView image;
    private String currentURL;
    private Context context;
    private String imageDate;
    private TextView textView;
    private RelativeLayout relativeLayout;
    private int id;
    private int currentWebcamWidth;
    private int currentWebcamHeight;
    private AsyncTask<Void, Void, Bitmap> downloadTask;
    private static String errorMessage;

    void startDownload(ImageView imageView, TextView txtView, int id, String url, Context cont, RelativeLayout loader) {
        image = imageView;
        textView = txtView;
        textView.setVisibility(View.INVISIBLE);
        currentURL = url;
        downloadTask = new DownloadPhoto(ImageDownloader.this).execute();
        context = cont;
        this.relativeLayout = loader;
        this.id = id;
    }

    private static class DownloadPhoto extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageDownloader> weakReference;

        DownloadPhoto(ImageDownloader imageDownloader) {
            weakReference = new WeakReference<>(imageDownloader);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            ImageDownloader imageDownloader = weakReference.get();
            imageDownloader.imageDate = "";
            Document doc;
            Document dateDoc = null;
            String tempUrl = null;

            switch (imageDownloader.id) {
                case 5:
                    tempUrl = "http://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html"; // INTE HTTPS!!!
                    break;
                case 6:
                    tempUrl = "http://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html"; // INTE HTTPS!!!
                    break;
                case 8:
                    tempUrl = "http://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html"; // INTE HTTPS!!!
                    break;
                case 9:
                    tempUrl = "http://www.valthorens.com/en/live/livecams--webcams/webcam-tsd-moutiere.414.html"; // INTE HTTPS!!!
                    break;
                case 10:
                    tempUrl = "http://www.valthorens.com/en/live/livecams--webcams/webcam-cime-caron.416.html"; // INTE HTTPS!!!
                    break;
            }

            try {
                if (tempUrl != null) {
                    dateDoc = Jsoup.connect(tempUrl).get();
                }
                doc = Jsoup.connect(imageDownloader.currentURL).get();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
                return null;
            }

            if (doc == null) {
                errorMessage = "Empty server response";
                return null;
            }
            if (imageDownloader.id < 5) {
                imageDownloader.currentWebcamWidth = 12755;
                imageDownloader.currentWebcamHeight = 2160;
                Elements scripts = doc.select("script");
                String script = "";
                for (Element d : scripts) {
                    if (d.toString().contains("new ImageMedia(")) {
                        script = d.toString();
                        break;
                    }
                }

                String[] imageLinks = script.split(",");
                for (int i = imageLinks.length - 1; i >= 0; i--) {
                    String s = imageLinks[i];
                    if (s.contains("new ImageMedia(\"//data.skaping.com/")) {
                        final String[] a = s.split("\"");
                        if (a.length > 1) {
                            imageDownloader.currentURL = "https:" + a[1];
                        } else {
                            imageDownloader.imageDate = "";
                            break;
                        }
                        imageDownloader.imageDate = imageDownloader.currentURL.replace("https://data.skaping.com/ValThorensBouquetin/", "")
                                .replace("https://data.skaping.com/funitelthorens-360/", "")
                                .replace("https://data.skaping.com/ValThorensLaMaison/", "")
                                .replace("https://data.skaping.com/vt2lacs-360/", "")
                                .replace(".jpg", "")
                                .replace("/", " ")
                                .replace("-", ":");
                        String[] temp = imageDownloader.imageDate.split(" ");
                        if (temp.length >= 4) {
                            imageDownloader.imageDate = "Taken at " + temp[0] + "-" + temp[1] + "-" + temp[2] + " " + temp[3] + ", CET";
                        } else {
                            imageDownloader.imageDate = "";
                        }
                        break;
                    } else if (s.contains("new ImageMedia(\"//storage.gra3.cloud.ovh.net")) {
                        imageDownloader.currentURL = "https:" + s.split("\"")[1];
                        final String[] arr = imageDownloader.currentURL.split("static/vt2lacs-360/");
                        if (arr.length > 1) {
                            imageDownloader.imageDate = arr[1]
                                    .replace(".jpg", "")
                                    .replace("/", " ")
                                    .replace("-", ":");
                        } else {
                            imageDownloader.imageDate = "";
                            break;
                        }

                        String[] temp = imageDownloader.imageDate.split(" ");
                        if (temp.length >= 4) {
                            imageDownloader.imageDate = "Taken at " + temp[0] + "-" + temp[1] + "-" + temp[2] + " " + temp[3] + ", CET";
                        } else {
                            imageDownloader.imageDate = "";
                        }
                        break;
                    }
                }
            } else {
                switch (imageDownloader.id) {
                    case 5:
                        imageDownloader.currentWebcamWidth = 11066;
                        imageDownloader.currentWebcamHeight = 2326;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_tyrolienne.jpg"; // INTE HTTPS!!!
                        break;
                    case 6:
                        imageDownloader.currentWebcamWidth = 7078;
                        imageDownloader.currentWebcamHeight = 1460;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg";
                        break;
                    case 7:
                        imageDownloader.currentWebcamWidth = 6243;
                        imageDownloader.currentWebcamHeight = 814;
                        imageDownloader.currentURL = "https://backend.roundshot.com/cams/232/default";
                        break;
                    case 8:
                        imageDownloader.currentWebcamWidth = 10000;
                        imageDownloader.currentWebcamHeight = 1986;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_funitel-bouquetin.jpg";
                        break;
                    case 9:
                        imageDownloader.currentWebcamWidth = 10000;
                        imageDownloader.currentWebcamHeight = 2042;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_cime_caron.jpg";
                        break;
                    case 10:
                        imageDownloader.currentWebcamWidth = 8346;
                        imageDownloader.currentWebcamHeight = 1543;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg"; // INTE HTTPS!!!
                        break;
                }

                if (imageDownloader.id != 7 && dateDoc != null) {
                    Elements date = dateDoc.select("p");
                    String script;
                    for (Element d : date) {
                        if (d.toString().contains("Last update : ")) {
                            script = d.text();
                            imageDownloader.imageDate = script;
                            String[] temp = imageDownloader.imageDate.split(" ");
                            if (temp.length >= 6) {
                                String[] arr = temp[5].split(":");
                                if (arr[0].length() == 1) {
                                    arr[0] = "0" + arr[0];
                                }
                                if (arr[1].length() == 1) {
                                    arr[1] = "0" + arr[1];
                                }
                                temp[5] = arr[0] + ":" + arr[1];
                                String[] datee = temp[3].split("/");
                                temp[3] = datee[2] + "-" + datee[1] + "-" + datee[0];
                                imageDownloader.imageDate = "Taken at " + temp[3] + " " + temp[5] + ", CET";
                            }
                        }
                    }
                } else {
                    imageDownloader.imageDate = "Updated every 10 minutes during daylight";
                }
            }
            try {
                int height = imageDownloader.getHeight();
                Resources r = imageDownloader.context.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, r.getDisplayMetrics()) * 4;
                double scaleWith = (height - px) / imageDownloader.currentWebcamHeight;

                return Picasso.with(imageDownloader.context)
                        .load(imageDownloader.currentURL)
                        .resize((int) (imageDownloader.currentWebcamWidth * scaleWith), (int) (imageDownloader.currentWebcamHeight * scaleWith))
                        .centerInside()
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageDownloader imageDownloader = weakReference.get();
            if (!isCancelled()) {
                if (bitmap == null) {
                    imageDownloader.showErrorDialog();
                } else {
                    imageDownloader.relativeLayout.setVisibility(View.GONE);
                    imageDownloader.image.setImageBitmap(bitmap);
                    imageDownloader.image.setVisibility(View.VISIBLE);
                    imageDownloader.textView.setText(imageDownloader.imageDate);
                    imageDownloader.textView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showErrorDialog() {
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    String message = "There seems to be trouble downloading the image, please try again later.\n\n";
                    if (errorMessage != null) {
                        message = message + "Error: " + errorMessage;
                    }
                    builder.setTitle("Error")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (context instanceof AppCompatActivity) {
                                        ((AppCompatActivity) context).finish();
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
        }
    }

    private int getHeight() {
        int height = 480;
        if (context instanceof WebcamActivity) {
            height = ((WebcamActivity) context).getHeight();
        } else if (context instanceof ChooseFromMapActivity) {
            height = ((ChooseFromMapActivity) context).getHeight();
        }
        return height;
    }

    void cancel() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }

}
