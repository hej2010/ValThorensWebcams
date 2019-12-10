package se.swecookie.valthorens;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private final int[] imageViews;
    private final List<Preview> previews;
    private final WeakReference<MainActivity> weakReference;

    MainAdapter(@NonNull MainActivity mainActivity, @NonNull List<Preview> previews) {
        this.imageViews = new int[]{R.drawable.choose_from_map, R.drawable.livecam_360, R.drawable.de_la_maison, R.drawable.les_2_lacs, R.drawable.funitel_de_thorens,
                R.drawable.funitel_3_vallees, R.drawable.stade, R.drawable.boismint, R.drawable.la_tyrolienne, R.drawable.plan_bouchet, R.drawable.plein_sud, R.drawable.cime_caron};
        this.previews = previews;
        this.weakReference = new WeakReference<>(mainActivity);
    }

    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_view, parent, false);
        return new MyViewHolder(relativeLayout);
    }

    /*
     * FUNITEL_3_VALLEES(1),  <meta property="og:image" content="http://storage.gra3.cloud.ovh.net/v1/AUTH_0e308f8996f940d38153db4d0e7d7e81/static/ValThorensBouquetin/2019/04/27/large/05-04.jpg"> // thumb
    DE_LA_MAISON(2), <meta property="og:image" content="http://data.skaping.com/ValThorensLaMaison/2019/09/17/large/11-02.jpg"> // thumb
    LES_2_LACS(3), <meta property="og:image" content="http://storage.gra3.cloud.ovh.net/v1/AUTH_0e308f8996f940d38153db4d0e7d7e81/static/vt2lacs-360/2019/07/31/large/09-22.jpg"> // thumb
    FUNITEL_DE_THORENS(4), <meta property="og:image" content="http://storage.gra3.cloud.ovh.net/v1/AUTH_0e308f8996f940d38153db4d0e7d7e81/static/funitelthorens-360/2019/09/04/large/13-22.jpg"> // thumb
    STADE(5), <meta property="og:image" content="http://storage.gra3.cloud.ovh.net/v1/AUTH_0e308f8996f940d38153db4d0e7d7e81/static/setam/stade-val-thorens/2019/06/10/large/16-22.jpg"> // thumb
    BOISMINT(6), <meta property="og:image" content="http://storage.gra3.cloud.ovh.net/v1/AUTH_0e308f8996f940d38153db4d0e7d7e81/static/val-thorens/boismint/2019/08/29/large/00-04.jpg"> // thumb
    LA_TYROLIENNE(7), http://www.trinum.com/ibox/ftpcam/small_val_thorens_tyrolienne.jpg
    PLAN_BOUCHET(8), http://www.trinum.com/ibox/ftpcam/small_orelle_sommet-tc-orelle.jpg
    LIVECAM_360(9),
    PLEIN_SUD(10), http://www.trinum.com/ibox/ftpcam/small_val_thorens_funitel-bouquetin.jpg
    CIME_CARON(11) http://www.trinum.com/ibox/ftpcam/small_val_thorens_cime-caron.jpg
     *
     */

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MainActivity context = weakReference.get();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context.isFinishing() || context.isDestroyed()) {
                return;
            }
        } else {
            if (context.isFinishing()) {
                return;
            }
        }
        holder.relativeLayout.setOnClickListener(view -> {
            final int id = holder.getAdapterPosition();
            if (id == 0) {
                context.startActivity(new Intent(context, ChooseFromMapActivity.class));
            } else {
                if (MainActivity.checkConnection(context, true)) {
                    Webcam clickedWebcam = MainActivity.webcams[id];
                    context.startActivity(new Intent(context, WebcamActivity.class)
                            .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
                }
            }
        });

        Picasso.get().load(imageViews[position])
                .placeholder(null)
                .into(holder.imageView);

        Preview p = previews.get(position);
        final boolean connection = MainActivity.checkConnection(context, false);
        final boolean correctWebcam = position != Webcam.CHOOSE_FROM_MAP.i && position != Webcam.LIVECAM_360.i;
        final boolean showPreviews = MainActivity.showPreviews;

        holder.fLPreview.setVisibility((connection || p.hasPreviewBeenShown())
                && correctWebcam && !p.isNotFound() && showPreviews ? View.VISIBLE : View.GONE);
        holder.progress.setVisibility(View.GONE);

        if (!correctWebcam || !showPreviews) {
            return;
        }
        if (p.hasPreviewBeenShown() || (connection && p.gotPreviewUrl())) {
            holder.progress.setVisibility(View.GONE);
            Picasso.get().load(p.getPreviewUrl())
                    .placeholder(null)
                    .into(holder.imgPreview, new Callback() {
                        @Override
                        public void onSuccess() {
                            p.setPreviewShown(true);
                        }

                        @Override
                        public void onError(Exception e) {
                            p.setPreviewShown(false);
                            e.printStackTrace();
                        }
                    });
            // TODO animate?
        } else if (connection && p.isNotLoading() && !p.gotPreviewUrl() && !p.isNotFound()) {
            holder.progress.setVisibility(View.VISIBLE);
            p.setLoading(true);
            new GetWebcamPreview(p, this, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (p.isNotLoading()) {
            p.setLoading(false);
            p.setPreviewShown(false);
            holder.imgPreview.setImageDrawable(null);
        }

    }

    private static class GetWebcamPreview extends AsyncTask<Void, Void, String> {
        private final Preview p;
        private final MainAdapter adapter;
        private final int position;

        private GetWebcamPreview(Preview p, MainAdapter adapter, int position) {
            this.p = p;
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {
            final Webcam webcam = Webcam.fromInt(position);
            String previewUrl = webcam.previewUrl;

            if (previewUrl == null && webcam.url != null && webcam != Webcam.LIVECAM_360) {
                Document doc;
                try {
                    doc = Jsoup.connect(webcam.url).ignoreContentType(true).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                if (doc == null) {
                    // empty response
                    return null;
                }
                Elements elements = doc.getElementsByTag("meta");
                for (Element e : elements) {
                    String html = e.outerHtml();
                    if (html.contains("=\"og:image\"") && html.contains("/large/")) {
                        String[] arr = html.split("\"");
                        if (arr.length > 3) {
                            previewUrl = arr[3].replace("/large/", "/thumb/");
                        }
                        break;
                    }
                }

            }
            return previewUrl;
        }

        @Override
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            if (url == null) {
                p.setLoading(false);
                p.setPreviewShown(false);
                p.setNotFound();
            } else {
                p.setLoading(false);
                p.setGotPreview();
                p.setPreviewUrl(url);
            }
            //Log.e("t " + position, "onPostExecute: " + url);

            adapter.notifyItemChanged(position);
        }

    }

    @Override
    public int getItemCount() {
        return imageViews.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView, imgPreview;
        final RelativeLayout relativeLayout;
        final FrameLayout fLPreview;
        final ProgressBar progress;

        MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgView);
            relativeLayout = v.findViewById(R.id.layout);
            imgPreview = v.findViewById(R.id.imgPreview);
            fLPreview = v.findViewById(R.id.fLPreview);
            progress = v.findViewById(R.id.progress);
        }
    }

}
