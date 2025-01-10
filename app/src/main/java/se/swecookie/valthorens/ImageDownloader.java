package se.swecookie.valthorens;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.swecookie.valthorens.data.Webcam;

class ImageDownloader {
    private static final String TAG = "ImageDownloader";
    private String currentURL;
    private AppCompatActivity context;
    private Webcam webcam;
    private AsyncTask<Void, Void, Void> downloadTask;
    private static String errorMessage;

    void startDownload(Webcam webcam, AppCompatActivity context, IOnImageDownloaded iOnImageDownloaded) {
        currentURL = webcam.url;
        downloadTask = new DownloadPhoto(ImageDownloader.this, iOnImageDownloaded)
                .execute();
        this.context = context;
        this.webcam = webcam;
    }

    private static class DownloadPhoto extends AsyncTask<Void, Void, Void> {
        private final ImageDownloader imageDownloader;
        private IOnImageDownloaded iOnImageDownloaded;
        private String imageDate = "";
        private String title = "";
        private String body = "";
        private static final Pattern datePattern = Pattern.compile("\\/(\\d\\d\\d\\d)\\/(\\d\\d)\\/(\\d\\d)\\/(\\d\\d)-(\\d\\d)"); // \/(\d\d\d\d)\/(\d\d)\/(\d\d)\/(\d\d)-(\d\d)

        DownloadPhoto(ImageDownloader imageDownloader, IOnImageDownloaded iOnImageDownloaded) {
            this.imageDownloader = imageDownloader;
            this.iOnImageDownloaded = iOnImageDownloaded;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document doc;

            try {
                Log.e(TAG, "doInBackground: " + imageDownloader.currentURL);
                Log.e(TAG, "doInBackground: " + imageDownloader.webcam.url + "; " + imageDownloader.webcam.previewUrl);
                doc = Jsoup.connect(imageDownloader.currentURL)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0")
                        .ignoreContentType(true)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
                Log.e(TAG, "doInBackground: error, return");
                if (imageDownloader.webcam.isStatic && imageDownloader.webcam.staticImageUrl != null) {
                    imageDownloader.currentURL = imageDownloader.webcam.staticImageUrl;
                }
                return null;
            }

            if (doc == null) {
                errorMessage = imageDownloader.context.getString(R.string.webcam_load_error_empty);
                cancel(true);
                return null;
            }

            try {
                if (!imageDownloader.webcam.isStatic) {
                    Log.e(TAG, "doInBackground: not static");
                    Elements scripts = doc.getElementsByTag("script");
                    Elements metaTags = doc.getElementsByTag("meta");

                    String script = "";
                    for (Element d : scripts) {
                        if (d.toString().contains("new ImageMedia(")) {
                            script = d.toString();
                            break;
                        }
                    }

                    for (Element metaTag : metaTags) {
                        String property = metaTag.attr("property");
                        if (property.equals("og:image")) {
                            String content = metaTag.attr("content");
                            imageDownloader.currentURL = content.replaceFirst("http://", "https://").replace("/large/", "/");

                            Matcher matcher = datePattern.matcher(imageDownloader.currentURL);
                            if (matcher.find()) {
                                Log.e(TAG, "doInBackground: matches " + matcher.groupCount());
                                if (matcher.groupCount() == 5) {
                                    imageDate = imageDownloader.context.getString(R.string.webcam_taken_at) + " " + matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3)
                                            + " " + matcher.group(4) + ":" + matcher.group(5) + ", CET";
                                }
                            }
                        }
                    }

                    // "messages":[{"title":"Webcam Hors Service","body":"Cette webcam est d\u00e9sactiv\u00e9e en raison du risque de foudre.\r\nRetour en novembre !",
                    // [{"title":"WEBCAM EN VACANCES","body":"\"En hibernation, de retours aux premi\u00e8res neiges ! \"",
                    String[] m = script.split("\"messages\":");
                    Log.e(TAG, "doInBackground: m is " + m.length);
                    if (m.length > 1) {
                        String s = m[1].split("\\}\\]")[0] + "}]";
                        JSONArray arr = new JSONArray(s);
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                JSONObject o = arr.getJSONObject(i);
                                if (o.has("title")) {
                                    title = fixDateString(o.getString("title"));
                                }
                                if (o.has("body")) {
                                    body = fixDateString(o.getString("body"));
                                }
                            } catch (JSONException ignored) {
                            }
                        }
                    }

                } else {
                    imageDownloader.currentURL = imageDownloader.webcam.staticImageUrl;
                    if (imageDownloader.currentURL == null) {
                        imageDownloader.currentURL = imageDownloader.webcam.url;
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
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.FRANCE);
                                    Date date2 = new Date();
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    String strDate = dateFormat.format(date2);
                                    boolean largerThan = false;
                                    try {
                                        largerThan = Integer.parseInt(d[1]) > 12;
                                    } catch (NumberFormatException ignored) {
                                    }
                                    if (d[0].equals(strDate) || largerThan) {
                                        String tmp = d[0];
                                        d[0] = d[1];
                                        d[1] = tmp;
                                    }
                                    if (d.length > 2) {
                                        date = d[2] + "-" + d[1] + "-" + d[0]; // yyyy-mm-dd
                                    }
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
                                imageDate = imageDownloader.context.getString(R.string.webcam_taken_at) + " " + date + " " + time + ", CET";
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        imageDate = imageDownloader.context.getString(R.string.webcam_10_minutes);
                    }

                    elements = doc.select(".panorama-view .alert .richText");
                    for (Element e : elements) {
                        if (e.toString().contains("<p>")) {
                            body = e.text().trim();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String fixDateString(String s) {
            Log.e(TAG, "fixDateString: before: " + s);
            return s.replace("\\u00e8", "è")
                    .replace("\\u00e9", "é")
                    .replace("\\r\\n", "\n")
                    .trim();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (imageDownloader == null) {
                return;
            }
            int height = imageDownloader.getHeight();

            if (height > 1500) {
                height = 1500;
            }

            iOnImageDownloaded.onImageInfoFound(imageDownloader.currentURL, height, imageDate, title, body, errorMessage);
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
