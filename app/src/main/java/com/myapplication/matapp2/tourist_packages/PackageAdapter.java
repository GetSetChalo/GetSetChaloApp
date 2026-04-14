package com.myapplication.matapp2.tourist_packages;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private final List<TouristPackage> packageList;
    private final String cityName;

    public PackageAdapter(List<TouristPackage> packageList, String cityName) {
        this.packageList = packageList;
        this.cityName = cityName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.myapplication.matapp2.R.layout.tourist_item_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TouristPackage pkg = packageList.get(position);
        holder.tvEmoji.setText(pkg.getEmoji());
        holder.tvDuration.setText(pkg.getDuration());
        holder.tvName.setText(pkg.getName());
        holder.tvRating.setText(pkg.getRating());
        holder.tvTags.setText(pkg.getTags());
        holder.tvDescription.setText(pkg.getDescription());
        holder.tvPrice.setText(pkg.getPrice());

        holder.btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("package", pkg);
            intent.putExtra("CITY_NAME", cityName);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return packageList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvDuration, tvName, tvRating, tvTags, tvDescription, tvPrice;
        Button btnSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji       = itemView.findViewById(com.myapplication.matapp2.R.id.tvEmoji);
            tvDuration    = itemView.findViewById(com.myapplication.matapp2.R.id.tvDuration);
            tvName        = itemView.findViewById(com.myapplication.matapp2.R.id.tvName);
            tvRating      = itemView.findViewById(com.myapplication.matapp2.R.id.tvRating);
            tvTags        = itemView.findViewById(com.myapplication.matapp2.R.id.tvTags);
            tvDescription = itemView.findViewById(com.myapplication.matapp2.R.id.tvDescription);
            tvPrice       = itemView.findViewById(com.myapplication.matapp2.R.id.tvPrice);
            btnSelect     = itemView.findViewById(com.myapplication.matapp2.R.id.btnSelect);
        }
    }
}
