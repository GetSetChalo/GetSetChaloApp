package com.myapplication.matapp2.tourist_packages;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    private Calendar checkInDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.myapplication.matapp2.R.layout.tourist_activity_detail);

        TouristPackage pkg = (TouristPackage) getIntent().getSerializableExtra("package");
        if (pkg == null) { finish(); return; }
        
        String cityName = getIntent().getStringExtra("CITY_NAME");

        // Header
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailEmoji)).setText(pkg.getEmoji());
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailDuration)).setText(pkg.getDuration());
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailName)).setText(pkg.getName());
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailRating)).setText(pkg.getRating());
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailTags)).setText(pkg.getTags());
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvDetailPrice)).setText(pkg.getPrice());

        // Inclusions
        LinearLayout inclusionContainer = findViewById(com.myapplication.matapp2.R.id.inclusionContainer);
        for (String item : pkg.getInclusions()) {
            TextView tv = new TextView(this);
            tv.setText("  " + item);
            tv.setTextColor(0xFFD0C8F0);
            tv.setTextSize(13f);
            tv.setLineSpacing(6f, 1f);
            tv.setPadding(0, 8, 0, 8);
            inclusionContainer.addView(tv);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(0x221E1650);
            inclusionContainer.addView(divider);
        }

        // Itinerary
        LinearLayout itineraryContainer = findViewById(com.myapplication.matapp2.R.id.itineraryContainer);
        List<String> itin = pkg.getItinerary();
        for (int i = 0; i < itin.size(); i++) {
            String line = itin.get(i);
            String[] parts = line.split("\\|", 2);
            String day  = parts.length > 0 ? parts[0].trim() : "Day " + (i + 1);
            String text = parts.length > 1 ? parts[1].trim() : line;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 14);
            row.setLayoutParams(rowParams);

            // Day badge
            TextView tvDay = new TextView(this);
            tvDay.setText(day);
            tvDay.setTextColor(0xFF2A1060);
            tvDay.setTextSize(10f);
            tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
            tvDay.setBackgroundResource(com.myapplication.matapp2.R.drawable.tourist_bg_button);
            tvDay.setPadding(16, 8, 16, 8);
            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            dayParams.setMarginEnd(12);
            tvDay.setLayoutParams(dayParams);

            // Description text
            TextView tvText = new TextView(this);
            tvText.setText(text);
            tvText.setTextColor(0xFF9B8FC0);
            tvText.setTextSize(12f);
            tvText.setLineSpacing(4f, 1f);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvText.setLayoutParams(textParams);

            row.addView(tvDay);
            row.addView(tvText);
            itineraryContainer.addView(row);
        }

        // Travelers Stepper logic
        final int[] travelers = {1};
        TextView tvTravelersCount = findViewById(com.myapplication.matapp2.R.id.tv_travelers_count);
        ImageButton btnMinus = findViewById(com.myapplication.matapp2.R.id.btn_travelers_minus);
        ImageButton btnPlus = findViewById(com.myapplication.matapp2.R.id.btn_travelers_plus);

        btnMinus.setOnClickListener(v -> {
            if (travelers[0] > 1) {
                travelers[0]--;
                tvTravelersCount.setText(String.format(java.util.Locale.US, "%02d", travelers[0]));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (travelers[0] < 10) {
                travelers[0]++;
                tvTravelersCount.setText(String.format(java.util.Locale.US, "%02d", travelers[0]));
            }
        });

        // Start Date logic
        LinearLayout layoutStartDate = findViewById(com.myapplication.matapp2.R.id.layout_start_date);
        TextView tvStartDate = findViewById(com.myapplication.matapp2.R.id.tv_start_date);
        
        layoutStartDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        checkInDate = Calendar.getInstance();
                        checkInDate.set(year, month, dayOfMonth);
                        tvStartDate.setText(String.format(Locale.US, "%02d/%02d/%04d", 
                                dayOfMonth, month + 1, year));
                    },
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        // Checkout button
        androidx.appcompat.widget.AppCompatButton btnBookNow = findViewById(com.myapplication.matapp2.R.id.btnBookNow);
        btnBookNow.setOnClickListener(v -> {
            if (checkInDate == null) {
                Toast.makeText(this, "Please select a start date", Toast.LENGTH_SHORT).show();
                return;
            }

            int nights = 1;
            try {
                String duration = pkg.getDuration();
                String[] parts = duration.split("/"); // e.g. ["3N ", " 4D"]
                for (String part : parts) {
                    part = part.trim().toUpperCase(Locale.US);
                    if (part.endsWith("N")) {
                        nights = Integer.parseInt(part.replace("N", "").trim());
                    }
                }
            } catch (Exception e) {}

            Calendar end = (Calendar) checkInDate.clone();
            end.add(Calendar.DAY_OF_YEAR, nights);

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
            String checkInStr = sdf.format(checkInDate.getTime());
            String checkOutStr = sdf.format(end.getTime());

            android.content.Intent intent = new android.content.Intent(DetailActivity.this, com.myapplication.matapp2.checkout_.CheckoutActivity.class);
            intent.putExtra("TRAVELERS", travelers[0]);
            intent.putExtra("BOOKING_TYPE", "PACKAGE");
            intent.putExtra("HOTEL_NAME", pkg.getName());
            intent.putExtra("CITY_NAME", cityName);
            intent.putExtra("CHECK_IN", checkInStr);
            intent.putExtra("CHECK_OUT", checkOutStr);
            intent.putExtra("DURATION_STR", pkg.getDuration());
            
            // Convert something like "₹8,499" into integer 8499
            String rawPrice = pkg.getPrice().replaceAll("[^0-9]", "");
            try {
                intent.putExtra("BASE_PRICE", Integer.parseInt(rawPrice) * travelers[0]);
            } catch (Exception e) {}

            startActivity(intent);
        });

        // Back button
        ImageButton btnBack = findViewById(com.myapplication.matapp2.R.id.btnDetailBack);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // ── Favourite button ───────────────────────────────────────────────────
        String pkgCity = cityName != null ? cityName : "";
        com.myapplication.matapp2.FavHelper.attachPackage(this, pkg.getName(), pkgCity);
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}