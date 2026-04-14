package com.myapplication.matapp2;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private final List<Hotel> hotelList;
    private final String city;

    public HotelAdapter(List<Hotel> hotelList, String city) {
        this.hotelList = hotelList;
        this.city = city;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_item, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        // ── Hotel name ──────────────────────────────────────────────────────────
        holder.hotelName.setText(hotel.getName());

        // ── Hotel image (Glide) ─────────────────────────────────────────────────
        Glide.with(holder.itemView.getContext())
                .load(hotel.getImageUrl())
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .into(holder.hotelImage);

        // ── Rating ──────────────────────────────────────────────────────────────
        // Format: " 4.0 ★"  (space before number, unicode filled star after)
        // Adjust score out of 10 (API format) down to a standard 5-star scale
        String rawRating = hotel.getRating();
        if (rawRating != null && !rawRating.isEmpty() && !rawRating.equals("0")) {
            try {
                double score = Double.parseDouble(rawRating);
                // Booking.com ratings are often out of 10. Scale down to 5.
                if (score > 5.0) {
                    score = score / 2.0;
                }
                DecimalFormat df = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
                holder.hotelRating.setText(" " + df.format(score) + " \u2605"); // ★
            } catch (NumberFormatException e) {
                holder.hotelRating.setText(" " + rawRating + " \u2605"); // ★
            }
            holder.hotelRating.setTextColor(Color.parseColor("#FFB800"));
            holder.hotelRating.setVisibility(View.VISIBLE);
        } else {
            holder.hotelRating.setVisibility(View.GONE);
        }

        // ── Price ───────────────────────────────────────────────────────────────
        // Convert currency code → symbol, then format the number with commas
        String currencySymbol = getCurrencySymbol(hotel.getCurrency());
        String formattedPrice = formatPrice(hotel.getPrice());
        holder.hotelPrice.setText(currencySymbol + formattedPrice);

        // "per Night" label is static in the layout; just ensure it's visible
        holder.hotelPriceLabel.setVisibility(View.VISIBLE);

        // ── Select button → navigates to HotelDetailActivity ───────────────────
        holder.btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HotelDetailActivity.class);
            intent.putExtra("HOTEL_ID",       hotel.getId());
            intent.putExtra("HOTEL_NAME",     hotel.getName());
            intent.putExtra("HOTEL_IMAGE",    hotel.getImageUrl());
            intent.putExtra("HOTEL_PRICE",    hotel.getPrice());
            intent.putExtra("HOTEL_CURRENCY", hotel.getCurrency());
            intent.putExtra("HOTEL_ADDRESS",  hotel.getAddress());
            intent.putExtra("CITY_NAME",      city);
            v.getContext().startActivity(intent);
        });

        // ── Card itself: no click listener (navigation is via Select button) ────
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────────

    /** Maps ISO 4217 currency code → local currency symbol. */
    private String getCurrencySymbol(String currencyCode) {
        if (currencyCode == null) return "$";
        switch (currencyCode.toUpperCase(Locale.US)) {
            case "INR": return "\u20B9";   // ₹
            case "GBP": return "\u00A3";   // £
            case "EUR": return "\u20AC";   // €
            case "AED": return "AED ";
            case "JPY": return "\u00A5";   // ¥
            case "AUD": return "A$";
            case "CAD": return "C$";
            default:    return "$";
        }
    }

    /**
     * Formats a raw price string (e.g. "8500.0" or "12000") with comma separators
     * and no fractional part when the decimal is zero.
     */
    private String formatPrice(String rawPrice) {
        if (rawPrice == null || rawPrice.isEmpty()) return "—";
        try {
            double value = Double.parseDouble(rawPrice);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat df = new DecimalFormat("#,##0", symbols);
            return df.format(value);
        } catch (NumberFormatException e) {
            return rawPrice; // fall back to raw string if not parseable
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ────────────────────────────────────────────────────────────────────────────

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView      hotelImage;
        TextView       hotelName;
        TextView       hotelRating;
        TextView       hotelPrice;
        TextView       hotelPriceLabel;
        MaterialButton btnSelect;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage      = itemView.findViewById(R.id.hotel_image);
            hotelName       = itemView.findViewById(R.id.hotel_name);
            hotelRating     = itemView.findViewById(R.id.hotel_rating);
            hotelPrice      = itemView.findViewById(R.id.hotel_price);
            hotelPriceLabel = itemView.findViewById(R.id.hotel_price_label);
            btnSelect       = itemView.findViewById(R.id.btn_select);
        }
    }
}