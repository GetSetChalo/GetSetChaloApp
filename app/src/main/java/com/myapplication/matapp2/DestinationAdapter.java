package com.myapplication.matapp2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {

    private Context context;
    private List<TouristDestination> destinationList;

    public DestinationAdapter(Context context, List<TouristDestination> destinationList) {
        this.context = context;
        this.destinationList = destinationList;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tw_item_destination, parent, false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        TouristDestination destination = destinationList.get(position);

        holder.tvName.setText(destination.getName());
        holder.tvLocation.setText(destination.getLocation());
        holder.tvRating.setText(destination.getRating());
        
        holder.tvPriceTitle.setText(destination.getPriceTitle());
        holder.tvPriceRange.setText(destination.getPriceRange());
        holder.tvPriceSubtext.setText(destination.getPriceSubtext());

        if (destination.isUnesco()) {
            holder.tvUnesco.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnesco.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(destination.getImageResId())
                .centerCrop()
                .into(holder.ivHeroImage);

        holder.btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(context, destination.getTargetActivity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public static class DestinationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHeroImage;
        TextView tvName, tvLocation, tvRating, tvUnesco, tvPriceTitle, tvPriceRange, tvPriceSubtext;
        Button btnSelect;

        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHeroImage = itemView.findViewById(R.id.ivHeroImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvUnesco = itemView.findViewById(R.id.tvUnesco);
            tvPriceTitle = itemView.findViewById(R.id.tvPriceTitle);
            tvPriceRange = itemView.findViewById(R.id.tvPriceRange);
            tvPriceSubtext = itemView.findViewById(R.id.tvPriceSubtext);
            btnSelect = itemView.findViewById(R.id.btnSelect);
        }
    }
}
