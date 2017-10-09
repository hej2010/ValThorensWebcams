package se.swecookie.valthorens;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
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

    void startDownload(ImageView imageView, TextView txtView, int id, String url, Context cont, RelativeLayout loader) {
        image = imageView;
        textView = txtView;
        textView.setVisibility(View.INVISIBLE);
        currentURL = url;
        new downloadPhoto().execute();
        context = cont;
        this.relativeLayout = loader;
        this.id = id;
    }

    private class downloadPhoto extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Document doc = null;
            Document dateDoc = null;
            String tempUrl = null;

            Log.e("id", "id " + id);

            switch (id) {
                case 5:
                    tempUrl = "https://www.valthorens.com/en/live/livecams--webcams/webcam-tyrolienne.648.html";
                    break;
                case 6:
                    tempUrl = "https://www.valthorens.com/en/live/livecams--webcams/webcam-plan-bouchet.704.html";
                    break;
                case 8:
                    tempUrl = "https://www.valthorens.com/en/live/livecams--webcams/webcam-folie-douce---plein-sud.418.html";
                    break;
                case 9:
                    tempUrl = "https://www.valthorens.com/en/live/livecams--webcams/webcam-tsd-moutiere.414.html";
                    break;
                case 10:
                    tempUrl = "https://www.valthorens.com/en/live/livecams--webcams/webcam-cime-caron.416.html";
                    break;
            }

            try {
                if (tempUrl != null) {
                    dateDoc = Jsoup.connect(tempUrl).timeout(20000).get();
                }
                doc = Jsoup.connect(currentURL).timeout(30000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert doc != null;
            if (id < 5) {
                currentWebcamWidth = 12755;
                currentWebcamHeight = 2160;
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
                        currentURL = "https:" + s.split("\"")[1];
                        imageDate = currentURL.replace("https://data.skaping.com/ValThorensBouquetin/", "")
                                .replace("https://data.skaping.com/funitelthorens-360/", "")
                                .replace("https://data.skaping.com/ValThorensLaMaison/", "")
                                .replace("https://data.skaping.com/vt2lacs-360/", "")
                                .replace(".jpg", "")
                                .replace("/", " ")
                                .replace("-", ":");
                        String[] temp = imageDate.split(" ");
                        if (temp.length >= 4) {
                            imageDate = "Taken at " + temp[0] + "-" + temp[1] + "-" + temp[2] + " " + temp[3] + ", CET";
                        } else {
                            imageDate = "";
                        }
                        break;
                    } else if (s.contains("new ImageMedia(\"//storage.gra3.cloud.ovh.net")) {
                        currentURL = "https:" + s.split("\"")[1];
                        Log.e("current", "url: " + currentURL);
                        imageDate = currentURL.split("static/funitelthorens-360/")[1]
                                .replace(".jpg", "")
                                .replace("/", " ")
                                .replace("-", ":");
                        String[] temp = imageDate.split(" ");
                        if (temp.length >= 4) {
                            imageDate = "Taken at " + temp[0] + "-" + temp[1] + "-" + temp[2] + " " + temp[3] + ", CET";
                        } else {
                            imageDate = "";
                        }
                        break;
                    }
                }
            } else if (id == 5 || id == 6 || id == 7 || id == 8) {
                switch (id) {
                    case 5:
                        currentWebcamWidth = 11066;
                        currentWebcamHeight = 2326;
                        currentURL = "https://www.trinum.com/ibox/ftpcam/mega_val_thorens_tyrolienne.jpg";
                        break;
                    case 6:
                        currentWebcamWidth = 7078;
                        currentWebcamHeight = 1460;
                        currentURL = "https://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg";
                        break;
                    case 7:
                        currentWebcamWidth = 6243;
                        currentWebcamHeight = 814;
                        currentURL = "https://backend.roundshot.com/cams/232/default";
                        break;
                    case 8:
                        currentWebcamWidth = 10000;
                        currentWebcamHeight = 1986;
                        currentURL = "https://www.trinum.com/ibox/ftpcam/mega_val_thorens_funitel-bouquetin.jpg";
                        break;
                    case 9:
                        currentWebcamWidth = 10000;
                        currentWebcamHeight = 2042;
                        currentURL = "https://www.trinum.com/ibox/ftpcam/mega_cime_caron.jpg";
                        break;
                    case 10:
                        currentWebcamWidth = 8346;
                        currentWebcamHeight = 1543;
                        currentURL = "https://www.trinum.com/ibox/ftpcam/mega_val_thorens_cime-caron.jpg"; // TODO lägg till kamera 9 och 10 !
                        break;
                }
                if (id != 7 && dateDoc != null) {
                    Elements date = dateDoc.select("p");
                    String script;
                    for (Element d : date) {
                        if (d.toString().contains("Last update : ")) {
                            script = d.text();
                            imageDate = script;
                            String[] temp = imageDate.split(" ");
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
                                imageDate = "Taken at " + temp[3] + " " + temp[5] + ", CET";
                            }
                        }
                    }
                } else {
                    imageDate = "Updated every 10 minutes during daylight";
                }
            }
            try {
                int height = getHeight();
                Resources r = context.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, r.getDisplayMetrics()) * 4;
                double scaleWith = (height - px) / currentWebcamHeight;

                Log.e("info ", "date: " + imageDate + "\nUrl: " + currentURL);

                return Picasso.with(context)
                        .load(currentURL)
                        .resize((int) (currentWebcamWidth * scaleWith), (int) (currentWebcamHeight * scaleWith))
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
            relativeLayout.setVisibility(View.GONE);
            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
            textView.setText(imageDate);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private int getHeight() {
        int height = 480;
        if (context instanceof Webcam) {
            height = ((Webcam) context).getHeight();
        } else if (context instanceof ChooseFromMap) {
            height = ((ChooseFromMap) context).getHeight();
        }
        return height;
    }

}
