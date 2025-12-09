package com.example.projectwmp.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch; // Import Switch
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Banner;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final Context context;
    private final List<Banner> bannerList;
    private final BannerClickListener listener;

    public interface BannerClickListener {
        void onBannerClick(Banner banner);
    }

    public BannerAdapter(Context context, List<Banner> bannerList, BannerClickListener listener) {
        this.context = context;
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asumsi item_banner_admin.xml adalah file yang Anda kirimkan
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_admin, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = bannerList.get(position);

        // Fiks: Menggunakan ID dari XML Anda
        holder.tvTitle.setText(banner.getTitle());
        holder.tvDescription.setText(banner.getDescription());
        holder.tvPriority.setText("Priority: " + banner.getPriority());
        holder.switchActive.setChecked(banner.isActive());

        // Listener untuk klik item keseluruhan
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });

        // Load image menggunakan Glide
        String imageUrl = banner.getFullImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = banner.getImageUrl();
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivImage);

        // Anda juga bisa menambahkan listener untuk Switch di sini jika Anda ingin mengubah status langsung dari list
        // holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     // Logic update status ke Firebase
        // });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    // Fiks: BannerViewHolder Class dengan ID XML yang benar
    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvDescription, tvPriority; // Tambah tvDescription
        Switch switchActive; // Ganti TextView tvStatus menjadi Switch

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            // MAPPING ID KE XML ANDA
            ivImage = itemView.findViewById(R.id.iv_banner); // iv_banner
            tvTitle = itemView.findViewById(R.id.tv_title); // tv_title
            tvDescription = itemView.findViewById(R.id.tv_description); // tv_description
            tvPriority = itemView.findViewById(R.id.tv_priority); // tv_priority
            switchActive = itemView.findViewById(R.id.switch_active); // switch_active
        }
    }
}