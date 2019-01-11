package se.swecookie.valthorens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.net.SocketTimeoutException;

class ImageDownloader {
    private ImageView image;
    private String currentURL;
    private Context context;
    private String imageDate;
    private TextView txtDate;
    private RelativeLayout rLLoading;
    private int id;
    private int currentWebcamWidth;
    private int currentWebcamHeight;
    private AsyncTask<Void, Void, Bitmap> downloadTask;
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

    private static class DownloadPhoto extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageDownloader> weakReference;

        DownloadPhoto(ImageDownloader imageDownloader) {
            weakReference = new WeakReference<>(imageDownloader);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            ImageDownloader imageDownloader = weakReference.get();
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
                imageDownloader.currentWebcamWidth = 12755; // 12755
                imageDownloader.currentWebcamHeight = 2160; // 2160
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
                        imageDownloader.currentWebcamWidth = 6775;
                        imageDownloader.currentWebcamHeight = 1110;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_tyrolienne.jpg";
                        break;
                    case 8:
                        imageDownloader.currentWebcamWidth = 7057;
                        imageDownloader.currentWebcamHeight = 1520;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg";
                        break;
                    case 9:
                        imageDownloader.currentWebcamWidth = 6136;
                        imageDownloader.currentWebcamHeight = 800;
                        imageDownloader.currentURL = "https://backend.roundshot.com/cams/232/default";
                        break;
                    case 10:
                        imageDownloader.currentWebcamWidth = 9999;
                        imageDownloader.currentWebcamHeight = 1986;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_funitel-bouquetin.jpg";
                        break;
                    case 11:
                        imageDownloader.currentWebcamWidth = 7140;
                        imageDownloader.currentWebcamHeight = 1586;
                        imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg";
                        break;
                }

                if (imageDownloader.id != 9) {
                    /*Elements date = doc.select("p");
                    String script;
                    boolean found = false;
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
                                found = true;
                                break;
                            }
                        }
                    }*/
                    Elements elements = doc.select(".webcam-navigation .desc");
                    String script;
                    boolean found = false;
                    Log.e("d", "d: " + elements.toString());
                    for (Element element : elements) {
                        if (element.toString().contains("/")) {
                            Log.e("d", "s: " + element.text());
                            script = element.text();
                            String[] temp = script.split(" ");
                            String date = null;
                            String time = null;
                            for (String s : temp) {
                                Log.e("s temp", "s: " + s);
                                if (s.contains("/") && s.split("/").length == 3) {
                                    Log.e("found", "found date: " + s);
                                    String[] d = s.split("/");
                                    if (d[1].length() == 1) {
                                        d[1] = "0" + d[1];
                                    }
                                    if (d[0].length() == 1) {
                                        d[0] = "0" + d[0];
                                    }
                                    date = d[2] + "-" + d[1] + "-" + d[0]; // yyyy-mm-dd
                                } else if (s.contains(":") && s.split(":").length == 2) {
                                    Log.e("found", "found time: " + s);
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
                        imageDownloader.imageDate = "Updates every 10 minutes during daylight";
                    }
                } else {
                    imageDownloader.imageDate = "Updates every 10 minutes during daylight";
                }
            }
            try {
                int height = imageDownloader.getHeight();

                if (height > 1500) {
                    height = 1500;
                }

                /*Resources r = imageDownloader.context.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, r.getDisplayMetrics()) * 4;
                double scaleWith = (height - px) / imageDownloader.currentWebcamHeight;*/

                return Picasso.get()
                        .load(imageDownloader.currentURL)
                        .resize(0 /*(int) (imageDownloader.currentWebcamWidth * scaleWith)*/, height/*(int) (imageDownloader.currentWebcamHeight * scaleWith)*/)
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
                    imageDownloader.rLLoading.setVisibility(View.GONE);
                    imageDownloader.image.setImageBitmap(bitmap);
                    imageDownloader.image.setVisibility(View.VISIBLE);
                    imageDownloader.txtDate.setText(imageDownloader.imageDate);
                    imageDownloader.txtDate.setVisibility(View.VISIBLE);
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
