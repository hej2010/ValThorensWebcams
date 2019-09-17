package se.swecookie.valthorens;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class MainAdapterDouble extends RecyclerView.Adapter<MainAdapterDouble.MyViewHolder> {
    private final List<DoubleInt> imageViews;
    private final List<DoublePreview> previews;
    private final WeakReference<MainActivity> weakReference;

    MainAdapterDouble(@NonNull MainActivity mainActivity, @NonNull List<Preview> previews) {
        this.imageViews = new ArrayList<>();
        imageViews.add(new DoubleInt(R.drawable.choose_from_map, R.drawable.funitel_3_vallees));
        imageViews.add(new DoubleInt(R.drawable.de_la_maison, R.drawable.les_2_lacs));
        imageViews.add(new DoubleInt(R.drawable.funitel_de_thorens, R.drawable.stade));
        imageViews.add(new DoubleInt(R.drawable.boismint, R.drawable.la_tyrolienne));
        imageViews.add(new DoubleInt(R.drawable.plan_bouchet, R.drawable.livecam_360));
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

        holder.imageView.setOnClickListener(view -> {
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
        holder.imageView2.setOnClickListener(view -> {
            final int id = holder.getAdapterPosition();
            if (MainActivity.checkConnection(context, true)) {
                Webcam clickedWebcam = MainActivity.webcams[id * 2 + 1];
                context.startActivity(new Intent(context, WebcamActivity.class)
                        .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
            }
        });

        DoublePreview p = previews.get(position);
        if (p.prv1.getPreviewUrl() != null) {
            Log.e("p " + position, "p != null: " + p.prv1.getPreviewUrl());
            Picasso.get().load(p.prv1.getPreviewUrl())
                    .placeholder(null)
                    .into(holder.imageView);
        } else if (p.prv1.isNotLoading() && !p.prv1.gotPreview()) {
            p.prv1.setLoading(true);
            Log.e("thread " + position, "before start");
            new Thread(() -> {
                Log.e("thread", "start");
                final Webcam webcam = Webcam.fromInt(position);
                String previewUrl = webcam.previewUrl;

                if (previewUrl == null && webcam.url != null && webcam != Webcam.LIVECAM_360) {
                    Document doc;
                    try {
                        doc = Jsoup.connect(webcam.url).ignoreContentType(true).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (doc == null) {
                        // empty response
                        return;
                    }
                    Elements elements = doc.getElementsByTag("meta");
                    for (Element e : elements) {
                        String html = e.outerHtml();
                        if (html.contains("=\"og:image\"")) {
                            previewUrl = html.split("\"")[3].replace("/large/", "/thumb/");
                            break;
                        }
                    }
                }
                if (previewUrl != null) {
                    Log.e("url", "position: " + position + " holdPos: " + holder.getAdapterPosition() + " ; " + previewUrl);
                    p.prv1.setPreviewUrl(previewUrl);

                    if (position == holder.getAdapterPosition()) {
                        String finalPreviewUrl = previewUrl;
                        context.runOnUiThread(() -> Picasso.get().load(finalPreviewUrl)
                                .placeholder(null)
                                .into(holder.imageView));

                    }
                    notifyItemRangeChanged(0, 12);
                }
                p.prv1.setGotPreview();
                p.prv1.setLoading(false);
            }).start();
        } else if ((p.prv1.isNotLoading() && p.prv1.gotPreview()) || p.prv1.getWebcam().url == null) {
            Log.e("p " + position, "!p.isLoading() && p.gotPreview()");
            Picasso.get().load(imageViews.get(position).img1)
                    .placeholder(null)
                    .into(holder.imageView);
        }
        // -------------------------------------------
        if (p.prv2.getPreviewUrl() != null) {
            Log.e("p " + position, "p != null: " + p.prv2.getPreviewUrl());
            Picasso.get().load(p.prv2.getPreviewUrl())
                    .placeholder(null)
                    .into(holder.imageView2);
        } else if (p.prv2.isNotLoading() && !p.prv2.gotPreview()) {
            p.prv2.setLoading(true);
            Log.e("thread " + position, "before start");
            new Thread(() -> {
                Log.e("thread", "start");
                final Webcam webcam = Webcam.fromInt(position);
                String previewUrl = webcam.previewUrl;

                if (previewUrl == null && webcam.url != null && webcam != Webcam.LIVECAM_360) {
                    Document doc;
                    try {
                        doc = Jsoup.connect(webcam.url).ignoreContentType(true).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    if (doc == null) {
                        // empty response
                        return;
                    }
                    Elements elements = doc.getElementsByTag("meta");
                    for (Element e : elements) {
                        String html = e.outerHtml();
                        if (html.contains("=\"og:image\"")) {
                            previewUrl = html.split("\"")[3].replace("/large/", "/thumb/");
                            break;
                        }
                    }
                }
                if (previewUrl != null) {
                    Log.e("url", "position: " + position + " holdPos: " + holder.getAdapterPosition() + " ; " + previewUrl);
                    p.prv2.setPreviewUrl(previewUrl);

                    if (position == holder.getAdapterPosition()) {
                        String finalPreviewUrl = previewUrl;
                        context.runOnUiThread(() -> Picasso.get().load(finalPreviewUrl)
                                .placeholder(null)
                                .into(holder.imageView2));

                    }
                    notifyItemRangeChanged(0, 12);
                }
                p.prv2.setGotPreview();
                p.prv2.setLoading(false);
            }).start();
        } else if ((p.prv2.isNotLoading() && p.prv2.gotPreview()) || p.prv2.getWebcam().url == null) {
            Log.e("p " + position, "!p.isLoading() && p.gotPreview()");
            Picasso.get().load(imageViews.get(position).img2)
                    .placeholder(null)
                    .into(holder.imageView2);
        }
    }

    @Override
    public int getItemCount() {
        return imageViews.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView, imageView2;

        MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgView);
            imageView2 = v.findViewById(R.id.imgView2);
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
