package se.swecookie.valthorens;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import se.swecookie.valthorens.data.Preview;
import se.swecookie.valthorens.data.Webcam;

@SuppressWarnings("ConstantConditions")
class MainAdapterDouble extends RecyclerView.Adapter<MainAdapterDouble.MyViewHolder> {
    private final List<DoubleInt> imageViews;
    private final List<DoublePreview> previews;
    private final WeakReference<MainActivity> weakReference;

    MainAdapterDouble(@NonNull MainActivity mainActivity, @NonNull List<Preview> previews) {
        this.imageViews = new ArrayList<>();
        imageViews.add(new DoubleInt(R.drawable.choose_from_map, R.drawable.livecam_360));
        imageViews.add(new DoubleInt(R.drawable.de_la_maison, R.drawable.les_2_lacs));
        imageViews.add(new DoubleInt(R.drawable.funitel_de_thorens, R.drawable.funitel_3_vallees));
        imageViews.add(new DoubleInt(R.drawable.stade, R.drawable.boismint));
        imageViews.add(new DoubleInt(R.drawable.la_tyrolienne, R.drawable.plan_bouchet));
        imageViews.add(new DoubleInt(R.drawable.plein_sud, R.drawable.cime_caron));

        this.previews = new ArrayList<>();
        this.previews.add(new DoublePreview(previews.get(0), previews.get(1)));
        this.previews.add(new DoublePreview(previews.get(2), previews.get(3)));
        this.previews.add(new DoublePreview(previews.get(4), previews.get(5)));
        this.previews.add(new DoublePreview(previews.get(6), previews.get(7)));
        this.previews.add(new DoublePreview(previews.get(8), previews.get(9)));
        this.previews.add(new DoublePreview(previews.get(10), previews.get(11)));
        this.weakReference = new WeakReference<>(mainActivity);
    }

    @NonNull
    @Override
    public MainAdapterDouble.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_view_wide, parent, false);
        return new MyViewHolder(linearLayout);
    }

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

        holder.rL1.setOnClickListener(view -> {
            final int id = holder.getAdapterPosition();
            if (id == 0) {
                context.startActivity(new Intent(context, ChooseFromMapActivity.class));
            } else {
                if (MainActivity.checkConnection(context, true)) {
                    Webcam clickedWebcam = MainActivity.webcams[id * 2];
                    context.startActivity(new Intent(context, WebcamActivity.class)
                            .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
                }
            }
        });
        holder.rL2.setOnClickListener(view -> {
            final int id = holder.getAdapterPosition();
            if (MainActivity.checkConnection(context, true)) {
                Webcam clickedWebcam = MainActivity.webcams[id * 2 + 1];
                context.startActivity(new Intent(context, WebcamActivity.class)
                        .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
            }
        });

        final DoubleInt di = imageViews.get(position);
        Picasso.get().load(di.img1)
                .placeholder(null)
                .into(holder.imageView);
        Picasso.get().load(di.img2)
                .placeholder(null)
                .into(holder.imageView2);

        DoublePreview p = previews.get(position);
        final boolean connection = MainActivity.checkConnection(context, false);
        final boolean correctWebcam1 = p.prv1.getWebcam().i != Webcam.CHOOSE_FROM_MAP.i && p.prv1.getWebcam().i != Webcam.LIVECAM_360.i;
        final boolean correctWebcam2 = p.prv2.getWebcam().i != Webcam.CHOOSE_FROM_MAP.i && p.prv2.getWebcam().i != Webcam.LIVECAM_360.i;
        final boolean showPreviews = MainActivity.showPreviews;

        holder.fLPreview1.setVisibility((connection || p.prv1.hasPreviewBeenShown())
                && correctWebcam1 && !p.prv1.isNotFound() && showPreviews ? View.VISIBLE : View.GONE);
        holder.fLPreview2.setVisibility((connection || p.prv2.hasPreviewBeenShown())
                && correctWebcam2 && !p.prv2.isNotFound() && showPreviews ? View.VISIBLE : View.GONE);
        holder.progress1.setVisibility(View.GONE);
        holder.progress2.setVisibility(View.GONE);

        if (correctWebcam1 && showPreviews) {
            if (p.prv1.hasPreviewBeenShown() || (connection && p.prv1.gotPreviewUrl())) {
                holder.progress1.setVisibility(View.GONE);
                Picasso.get().load(p.prv1.getPreviewUrl())
                        .placeholder(null)
                        .into(holder.imgPreview1, new Callback() {
                            @Override
                            public void onSuccess() {
                                p.prv1.setPreviewShown(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                p.prv1.setPreviewShown(false);
                                e.printStackTrace();
                            }
                        });
                // TODO animate?
            } else if (connection && p.prv1.isNotLoading() && !p.prv1.gotPreviewUrl() && !p.prv1.isNotFound()) {
                holder.progress1.setVisibility(View.VISIBLE);
                p.prv1.setLoading(true);
                Webcam w = Webcam.fromInt(position * 2);
                new GetWebcamPreview(p.prv1, this, position, w).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (p.prv1.isNotLoading()) {
                p.prv1.setLoading(false);
                p.prv1.setPreviewShown(false);
                holder.imgPreview1.setImageDrawable(null);
            }
        }
        if (correctWebcam2 && showPreviews) {
            if (p.prv2.hasPreviewBeenShown() || (connection && p.prv2.gotPreviewUrl())) {
                holder.progress2.setVisibility(View.GONE);
                Picasso.get().load(p.prv2.getPreviewUrl())
                        .placeholder(null)
                        .into(holder.imgPreview2, new Callback() {
                            @Override
                            public void onSuccess() {
                                p.prv2.setPreviewShown(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                p.prv2.setPreviewShown(false);
                                e.printStackTrace();
                            }
                        });
                // TODO animate?
            } else if (connection && p.prv2.isNotLoading() && !p.prv2.gotPreviewUrl() && !p.prv2.isNotFound()) {
                holder.progress2.setVisibility(View.VISIBLE);
                p.prv2.setLoading(true);
                Webcam w = Webcam.fromInt(position * 2 + 1);
                new GetWebcamPreview(p.prv2, this, position, w).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (p.prv2.isNotLoading()) {
                p.prv2.setLoading(false);
                p.prv2.setPreviewShown(false);
                holder.imgPreview2.setImageDrawable(null);
            }
        }
    }

    private static class GetWebcamPreview extends AsyncTask<Void, Void, String> {
        private final Preview p;
        private final MainAdapterDouble adapter;
        private final int adapterPosition;
        private final Webcam webcam;

        private GetWebcamPreview(Preview p, MainAdapterDouble adapter, int adapterPosition, Webcam webcam) {
            this.p = p;
            this.adapter = adapter;
            this.adapterPosition = adapterPosition;
            this.webcam = webcam;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String previewUrl = webcam.previewUrl;

            //Log.e("d " + webcam.i, "doInBackground: " + webcam.url);
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
                    // Log.e("d " + webcam.i, "doc == null");
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
            //Log.e("t " + webcam.i, "onPostExecute: " + url);

            adapter.notifyItemChanged(adapterPosition);
        }

    }

    @Override
    public int getItemCount() {
        return imageViews.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView, imageView2, imgPreview1, imgPreview2;
        final FrameLayout fLPreview1, fLPreview2;
        final CardView rL1, rL2;
        final ProgressBar progress1, progress2;

        MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgView1);
            imageView2 = v.findViewById(R.id.imgView2);
            fLPreview1 = v.findViewById(R.id.fLPreview1);
            fLPreview2 = v.findViewById(R.id.fLPreview2);
            imgPreview1 = v.findViewById(R.id.imgPreview1);
            imgPreview2 = v.findViewById(R.id.imgPreview2);
            rL1 = v.findViewById(R.id.rL1);
            rL2 = v.findViewById(R.id.rL2);
            progress1 = v.findViewById(R.id.progress1);
            progress2 = v.findViewById(R.id.progress2);
        }
    }

    private static class DoubleInt {
        final int img1, img2;

        private DoubleInt(int img1, int img2) {
            this.img1 = img1;
            this.img2 = img2;
        }
    }

    private static class DoublePreview {
        final Preview prv1, prv2;

        private DoublePreview(Preview prv1, Preview prv2) {
            this.prv1 = prv1;
            this.prv2 = prv2;
        }
    }

}
