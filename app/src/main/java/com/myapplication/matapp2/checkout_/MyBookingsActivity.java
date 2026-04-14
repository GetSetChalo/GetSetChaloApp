package com.myapplication.matapp2.checkout_;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.myapplication.matapp2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyBookingsActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    private RecyclerView recyclerView;
    private View emptyState;
    private final List<BookingInfo> bookingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_my_bookings);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.recyclerViewDeals); // using ID from XML
        emptyState = findViewById(R.id.emptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadBookings();
    }

    private void loadBookings() {
        SharedPreferences prefs = getSharedPreferences("BookingsPrefs", MODE_PRIVATE);
        String savedData = prefs.getString("BookingsArray", "[]");

        try {
            JSONArray array = new JSONArray(savedData);
            for (int i = array.length() - 1; i >= 0; i--) { // Reverse order (newest first)
                JSONObject obj = array.getJSONObject(i);
                bookingsList.add(new BookingInfo(
                        obj.optString("title", "Unknown"),
                        obj.optString("city", ""),
                        obj.optString("type", "HOTEL"),
                        obj.optString("dates", ""),
                        obj.optInt("total", 0)
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (bookingsList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new MyBookingsAdapter(bookingsList));
        }
    }

    @Override
    protected String getActiveTab() {
        return "bookings";
    }

    // --- Simple Data Model ---
    public static class BookingInfo {
        String title, city, type, dates;
        int totalAmount;

        public BookingInfo(String title, String city, String type, String dates, int totalAmount) {
            this.title = title;
            this.city = city;
            this.type = type;
            this.dates = dates;
            this.totalAmount = totalAmount;
        }
    }

    // --- Adapter inline ---
    public static class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.ViewHolder> {
        private final List<BookingInfo> data;
        
        public MyBookingsAdapter(List<BookingInfo> data) {
            this.data = data;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_booking, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BookingInfo info = data.get(position);
            holder.tvBookingTitle.setText(info.title);
            
            if (info.city != null && !info.city.isEmpty()) {
                String capCity = info.city.substring(0, 1).toUpperCase(Locale.US) + info.city.substring(1).toLowerCase(Locale.US);
                holder.tvBookingCity.setText(capCity);
                holder.tvBookingCity.setVisibility(View.VISIBLE);
            } else {
                holder.tvBookingCity.setVisibility(View.GONE);
            }
            
            if ("PACKAGE".equals(info.type)) {
                holder.tvBookingIcon.setText("✈️");
            } else {
                holder.tvBookingIcon.setText("🏨");
            }
            
            holder.tvBookingDates.setText(info.dates);
            String formattedCost = NumberFormat.getNumberInstance(new Locale("en", "IN")).format(info.totalAmount);
            holder.tvBookingTotal.setText("₹" + formattedCost);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookingIcon, tvBookingTitle, tvBookingCity, tvBookingDates, tvBookingTotal;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBookingIcon  = itemView.findViewById(R.id.tvBookingIcon);
                tvBookingTitle = itemView.findViewById(R.id.tvBookingTitle);
                tvBookingCity  = itemView.findViewById(R.id.tvBookingCity);
                tvBookingDates = itemView.findViewById(R.id.tvBookingDates);
                tvBookingTotal = itemView.findViewById(R.id.tvBookingTotal);
            }
        }
    }
}
