package se.swecookie.valthorens;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
        this.imageViews = new int[]{R.drawable.choose_from_map, R.drawable.funitel_3_vallees, R.drawable.de_la_maison, R.drawable.les_2_lacs, R.drawable.funitel_de_thorens, R.drawable.stade,
                R.drawable.boismint, R.drawable.la_tyrolienne, R.drawable.plan_bouchet, R.drawable.livecam_360, R.drawable.plein_sud, R.drawable.cime_caron};
        this.previews = previews;
        this.weakReference = new WeakReference<>(mainActivity);
    }

    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_view, parent, false);
        return new MyViewHolder(frameLayout);
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
        holder.frameLayout.setOnClickListener(view -> {
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

        Preview p = previews.get(position);
        final boolean connection = MainActivity.checkConnection(context, false);

        if (!connection || p.getWebcam() == Webcam.LIVECAM_360 || p.getWebcam() == Webcam.CHOOSE_FROM_MAP) {
            holder.txtTitle.setText("");
        } else {
            holder.txtTitle.setText(p.getWebcam().name);
        }

        Picasso.get().load(imageViews[position])
                .placeholder(null)
                .into(holder.imageView);
        if (p.getPreviewUrl() != null && connection) {
            Log.e("p " + position, "p != null: " + p.getPreviewUrl());
            Picasso.get().load(p.getPreviewUrl())
                    .placeholder(null)
                    .into(holder.imageView);
        } else if (p.isNotLoading() && !p.gotPreview() && connection) {
            p.setLoading(true);
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
                        p.setGotPreview();
                        p.setLoading(false);
                        return;
                    }
                    if (doc == null) {
                        // empty response
                        p.setGotPreview();
                        p.setLoading(false);
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
                    p.setPreviewUrl(previewUrl);

                    if (position == holder.getAdapterPosition()) {
                        String finalPreviewUrl = previewUrl;
                        context.runOnUiThread(() -> Picasso.get().load(finalPreviewUrl)
                                .placeholder(null)
                                .into(holder.imageView));

                    }
                    holder.frameLayout.post(() -> notifyItemRangeChanged(0, 12));
                }
                p.setGotPreview();
                p.setLoading(false);
            }).start();
        } else if (!connection || (p.isNotLoading() && p.gotPreview()) || p.getWebcam().url == null) {
            Log.e("p " + position, "!p.isLoading() && p.gotPreview()");
            Picasso.get().load(imageViews[position])
                    .placeholder(null)
                    .into(holder.imageView);
        } else {
            Log.e("p " + position, "else");
        }

    }

    @Override
    public int getItemCount() {
        return imageViews.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final FrameLayout frameLayout;
        final TextView txtTitle;

        MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgView);
            frameLayout = v.findViewById(R.id.layout);
            txtTitle = v.findViewById(R.id.txtTitle);
        }
    }

}
