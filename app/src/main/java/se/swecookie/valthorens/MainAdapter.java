package se.swecookie.valthorens;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private final int[] imageViews;

    MainAdapter() {
        this.imageViews = new int[]{R.drawable.choose_from_map, R.drawable.funitel_3_vallees, R.drawable.de_la_maison, R.drawable.les_2_lacs, R.drawable.funitel_de_thorens, R.drawable.stade,
                R.drawable.boismint, R.drawable.la_tyrolienne, R.drawable.plan_bouchet, R.drawable.livecam_360, R.drawable.plein_sud, R.drawable.cime_caron};
    }

    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_view, parent, false);
        final MyViewHolder holder = new MyViewHolder(linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int id = holder.getAdapterPosition();
                if (id == 0) {
                    parent.getContext().startActivity(new Intent(parent.getContext(), ChooseFromMapActivity.class));
                } else {
                    if (MainActivity.checkConnection(parent.getContext())) {
                        Webcam clickedWebcam = MainActivity.webcams[id];
                        parent.getContext().startActivity(new Intent(parent.getContext(), WebcamActivity.class)
                                .putExtra(WebcamActivity.EXTRA_WEBCAM, clickedWebcam));
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Picasso.get().load(imageViews[position])
                .placeholder(null)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageViews.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MyViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imgView);
        }
    }

}
