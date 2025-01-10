package se.swecookie.valthorens;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
        downloadTask = new DownloadPhoto(ImageDownloader.this, iOnImageDownloaded).execute();
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
                doc = Jsoup.connect(imageDownloader.currentURL).ignoreContentType(true).get();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
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
                        String[] m2 = m[1].split("\\}\\]");
                        if (m2.length > 1) {
                            // now we should have: [{"title":"Webcam Hors Service","body":"Cette webcam est d\u00e9sactiv\u00e9e en raison du risque de foudre.\r\nRetour en novembre !","link":null,"delay":null
                            String[] m3 = m2[0].split("\"");
                            Log.e(TAG, "doInBackground: " + m2[0]);
                            if (m3.length > 7) {
                                title = fixDateString(m3[3]);
                                body = fixDateString(m3[7]);
                            }
                        }
                    }

                } else {
                    switch (imageDownloader.webcam) {
                        case LA_TYROLIENNE:
                            imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_tyrolienne.jpg";
                            break;
                        case PLAN_BOUCHET:
                            imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg";
                            break;
                        case LIVECAM_360:
                            imageDownloader.currentURL = "http://backend.roundshot.com/cams/232/default";
                            break;
                        case PLEIN_SUD:
                            imageDownloader.currentURL = "http://www.trinum.com/ibox/ftpcam/mega_val_thorens_funitel-bouquetin.jpg";
                            break;
                        case CIME_CARON:
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
