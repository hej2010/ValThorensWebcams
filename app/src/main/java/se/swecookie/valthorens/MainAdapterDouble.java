package se.swecookie.valthorens;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MainAdapterDouble extends RecyclerView.Adapter<MainAdapterDouble.MyViewHolder> {
    private final ArrayList<DoubleInt> imageViews;

    MainAdapterDouble() {
        this.imageViews = new ArrayList<>();
        imageViews.add(new DoubleInt(R.drawable.choose_from_map, R.drawable.funitel_3_vallees));
        imageViews.add(new DoubleInt(R.drawable.de_la_maison, R.drawable.les_2_lacs));
        imageViews.add(new DoubleInt(R.drawable.funitel_de_thorens, R.drawable.stade));
        imageViews.add(new DoubleInt(R.drawable.boismint, R.drawable.la_tyrolienne));
        imageViews.add(new DoubleInt(R.drawable.plan_bouchet, R.drawable.livecam_360));
        imageViews.add(new DoubleInt(R.drawable.plein_sud, R.drawable.cime_caron));
    }

    @NonNull
    @Override
    public MainAdapterDouble.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_view_wide, parent, false);
        return new MyViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.imageView.setOnClickListener(view -> {
            final Context context = view.getContext();
            final int id = holder.getAdapterPosition();
            if (id == 0) {
                context.startActivity(new Intent(context, ChooseFromMapActivity.class));
            } else {
                if (MainActivity.checkConnection(context)) {
                    Webcam clickedWebcam = MainActivity.webcams[id * 2];
                    context.startActivity(new Intent(context, WebcamActivity.class)
                            .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
                }
            }
        });
        holder.imageView2.setOnClickListener(view -> {
            final Context context = view.getContext();
            final int id = holder.getAdapterPosition();
            if (MainActivity.checkConnection(context)) {
                Webcam clickedWebcam = MainActivity.webcams[id * 2 + 1];
                context.startActivity(new Intent(context, WebcamActivity.class)
                        .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
            }
        });
        Picasso.get().load(imageViews.get(position).img1)
                .placeholder(null)
                .into(holder.imageView);
        Picasso.get().load(imageViews.get(position).img2)
                .placeholder(null)
                .into(holder.imageView2);
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

}
