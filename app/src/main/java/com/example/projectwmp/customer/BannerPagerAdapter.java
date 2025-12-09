package com.example.projectwmp.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.projectwmp.R;
import com.example.projectwmp.models.Banner;
import java.util.List;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.BannerViewHolder> {

    private final Context context;
    private final List<Banner> bannerList;
    private final OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }

    public BannerPagerAdapter(Context context, List<Banner> bannerList, OnBannerClickListener listener) {
        this.context = context;
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_slider, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = bannerList.get(position);

        // Set banner title
        if (banner.getTitle() != null && !banner.getTitle().isEmpty()) {
            holder.tvBannerTitle.setText(banner.getTitle());
            holder.tvBannerTitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvBannerTitle.setVisibility(View.GONE);
        }

        // Set banner description
        String desc = banner.getDescription();
        if (desc != null && !desc.isEmpty()) {
            holder.tvBannerDescription.setText(desc);
            holder.tvBannerDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvBannerDescription.setVisibility(View.GONE);
        }

        // Load banner image with Glide
        String imageUrl = banner.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(holder.ivBanner);
        } else {
            holder.ivBanner.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvBannerTitle, tvBannerDescription;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvBannerTitle = itemView.findViewById(R.id.tv_banner_title);
            tvBannerDescription = itemView.findViewById(R.id.tv_banner_description);
        }
    }
}