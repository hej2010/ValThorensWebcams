package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

class ImageDownloader {
    private ImageView image;
    private String currentURL;
    private Context context;
    private String imageDate;
    private TextView txtDate;
    private RelativeLayout rLLoading;
    private int id;
    private AsyncTask<Void, Void, Void> downloadTask;
    private static String errorMessage;

    void startDownload(ImageView imageView, TextView txtView, int id, String url, Context cont, RelativeLayout loader) {
        image = imageView;
        txtDate = txtView;
        txtDate.setVisibility(View.INVISIBLE);
        currentURL = url;
        downloadTask = new DownloadPhoto(ImageDownloader.this).execute();
        context = cont;
        this.rLLoading = loader;
        this.id = id;
    }

    private static class DownloadPhoto extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ImageDownloader> weakReference;

        DownloadPhoto(ImageDownloader imageDownloader) {
            weakReference = new WeakReference<>(imageDownloader);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final ImageDownloader imageDownloader = weakReference.get();
            imageDownloader.imageDate = "";
            Document doc = null;

            try {
                doc = Jsoup.connect(imageDownloader.currentURL).ignoreContentType(true).get();
            } catch (SocketTimeoutException e) {
                errorMessage = e.getMessage();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (doc == null) {
                errorMessage = "Empty server response";
                cancel(true);
                return null;
            }
            if (imageDownloader.id < 7) {
                Elements scripts = doc.getElementsByTag("script");
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
                                .replace("https://data.skaping.com/setam/stade-val-thorens/", "")
                                .replace("https://data.skaping.com/val-thorens/boismint/", "")
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
                        final String[] arr = imageDownloader.currentURL.split("/");
                        if (arr.length > 4) {
                            imageDownloader.imageDate = "Taken at " + arr[arr.length - 4] + "-" + arr[arr.length - 3] + "-" + arr[arr.length - 2]
                                    + " " + arr[arr.length - 1].substring(0, 2) + ":" + arr[arr.length - 1].substring(3, 5) + ", CET";
                        } else {
                            imageDownloader.imageDate = "";
                            break;
                        }
                        break;
                    }
                }
            } else {
                switch (imageDownloader.id) {
                    case 7:
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_tyrolienne.jpg";
                        break;
                    case 8:
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg";
                        break;
                    case 9:
                        imageDownloader.currentURL = "http://backend.roundshot.com/cams/232/default";
                        break;
                    case 10:
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_funitel-bouquetin.jpg";
                        break;
                    case 11:
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg";
                        break;
                }

                boolean found = false;
                Elements elements = doc.select(".webcam-navigation .desc");
                String script;
                for (Element element : elements) {
                    if (element.toString().contains("/")) {
                        script = element.text();
                        String[] temp = script.split(" ");
                        String date = null;
                        String time = null;
                        for (String s : temp) {
                            if (s.contains("/") && s.split("/").length == 3) {
                                String[] d = s.split("/");
                                if (d[1].length() == 1) {
                                    d[1] = "0" + d[1];
                                }
                                if (d[0].length() == 1) {
                                    d[0] = "0" + d[0];
                                }
                                date = d[2] + "-" + d[1] + "-" + d[0]; // yyyy-mm-dd
                            } else if (s.contains(":") && s.split(":").length == 2) {
                                String[] t = s.split(":");
                                if (t[0].length() == 1) {
                                    t[0] = "0" + t[0];
                                }
                                if (t[1].length() == 1) {
                                    t[1] = "0" + t[1];
                                }
                                time = t[0] + ":" + t[1]; //hh:mm
                            }
                        }
                        if (date != null && time != null) {
                            imageDownloader.imageDate = "Taken at " + date + " " + time + ", CET";
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    imageDownloader.imageDate = imageDownloader.context.getString(R.string.webcam_10_minutes);
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final ImageDownloader imageDownloader = weakReference.get();
            int height = imageDownloader.getHeight();

            if (height > 1500) {
                height = 1500;
            }

            Picasso.get()
                    .load(imageDownloader.currentURL)
                    .resize(0, height)
                    .centerInside()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageDownloader.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageDownloader.rLLoading.setVisibility(View.GONE);
                            imageDownloader.image.setVisibility(View.VISIBLE);
                            imageDownloader.txtDate.setText(imageDownloader.imageDate);
                            imageDownloader.txtDate.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            imageDownloader.showErrorDialog();
                        }
                    });
        }
    }

    private void showErrorDialog() {
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
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
