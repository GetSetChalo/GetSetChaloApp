package com.myapplication.matapp2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TourismWaysActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    // ── Views ────────────────────────────────────────────────────
    private RecyclerView recyclerView;
    private LinearLayout layoutComingSoon;
    private ProgressBar progressBar;
    private TextView tvSectionLabel;
    private TextView tabHotels, tabPackages, tabDestinations;

    // ── Data ─────────────────────────────────────────────────────
    private HotelAdapter adapter;
    private final List<Hotel> hotelList = new ArrayList<>();
    private boolean hotelsFetched = false;   // fetch once, cache after
    private String cityName = "";

    // ── API ──────────────────────────────────────────────────────
    private final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY =
            "cf0f3280dcmshe074eb408bbeeffp1305d1jsnc0b544949a98";

    // ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourism_ways);

        cityName = getIntent().getStringExtra("CITY_NAME");
        if (cityName == null) cityName = "";

        bindViews();
        setupHeader();
        setupTabs();
        selectTab(0);   // start on Hotels
    }

    // ─────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────

    private void bindViews() {
        tvSectionLabel   = findViewById(R.id.tvSectionLabel);
        recyclerView     = findViewById(R.id.twRecyclerView);
        layoutComingSoon = findViewById(R.id.twComingSoon);
        progressBar      = findViewById(R.id.twProgressBar);
        tabHotels        = findViewById(R.id.tabHotels);
        tabPackages      = findViewById(R.id.tabPackages);
        tabDestinations  = findViewById(R.id.tabDestinations);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotelAdapter(hotelList, cityName);
        recyclerView.setAdapter(adapter);
    }

    // ─────────────────────────────────────────────────────────────
    // Header
    // ─────────────────────────────────────────────────────────────

    private void setupHeader() {
        TextView tvCity = findViewById(R.id.tvHeaderCity);
        tvCity.setText(formatCity(cityName));

        findViewById(R.id.btnTwBack)
                .setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        findViewById(R.id.btnTwProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this,
                    com.myapplication.matapp2.checkout_.ProfileActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    this, android.R.anim.slide_in_left, android.R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Tabs
    // ─────────────────────────────────────────────────────────────

    private void setupTabs() {
        tabHotels      .setOnClickListener(v -> selectTab(0));
        tabPackages    .setOnClickListener(v -> selectTab(1));
        tabDestinations.setOnClickListener(v -> selectTab(2));
    }

    private void selectTab(int tab) {
        // Reset all to inactive
        tabHotels.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_left_inactive));
        tabHotels.setTextColor(android.graphics.Color.parseColor("#FFC107"));

        tabPackages.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_middle_inactive));
        tabPackages.setTextColor(android.graphics.Color.parseColor("#FFC107"));

        tabDestinations.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_right_inactive));
        tabDestinations.setTextColor(android.graphics.Color.parseColor("#FFC107"));

        String city = formatCity(cityName);

        switch (tab) {
            case 0:
                tabHotels.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_left_active));
                tabHotels.setTextColor(android.graphics.Color.parseColor("#1A0D3A"));
                tvSectionLabel.setText("Hotels in " + city);
                showHotels();
                break;
            case 1:
                tabPackages.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_middle_active));
                tabPackages.setTextColor(android.graphics.Color.parseColor("#1A0D3A"));
                tvSectionLabel.setText("Tourism Packages in " + city);
                showPackages();
                break;
            case 2:
                tabDestinations.setBackground(ContextCompat.getDrawable(this, R.drawable.tw_tab_right_active));
                tabDestinations.setTextColor(android.graphics.Color.parseColor("#1A0D3A"));
                tvSectionLabel.setText("Tourist Destinations in " + city);
                showDestinations();
                break;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Content switching
    // ─────────────────────────────────────────────────────────────

    private void showHotels() {
        layoutComingSoon.setVisibility(View.GONE);

        if (hotelsFetched) {
            // Restore hotel adapter
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        if (isCitySupported(cityName)) {
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            startSearchProcess(cityName);
        } else {
            recyclerView.setVisibility(View.GONE);
            layoutComingSoon.setVisibility(View.VISIBLE);
        }
    }

    private void showPackages() {
        layoutComingSoon.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        
        String cityLower = cityName.toLowerCase(Locale.US).trim();
        List<com.myapplication.matapp2.tourist_packages.TouristPackage> list = 
                com.myapplication.matapp2.tourist_packages.PackageData.getAllPackages().get(cityLower);
                
        if (list != null && !list.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new com.myapplication.matapp2.tourist_packages.PackageAdapter(list, cityName));
        } else {
            recyclerView.setVisibility(View.GONE);
            layoutComingSoon.setVisibility(View.VISIBLE);
        }
    }

    private void showDestinations() {
        layoutComingSoon.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        List<TouristDestination> list = DestinationData.getDestinationsForCity(cityName);

        if (list != null && !list.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new DestinationAdapter(this, list));
        } else {
            recyclerView.setVisibility(View.GONE);
            layoutComingSoon.setVisibility(View.VISIBLE);
        }
    }

    private void showComingSoon() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        layoutComingSoon.setVisibility(View.VISIBLE);
    }

    // ─────────────────────────────────────────────────────────────
    // City helpers
    // ─────────────────────────────────────────────────────────────

    private String formatCity(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        return raw.substring(0, 1).toUpperCase(Locale.US)
                + raw.substring(1).toLowerCase(Locale.US);
    }

    private boolean isCitySupported(String city) {
        String q = city.toLowerCase(Locale.US).trim();
        return q.equals("jaipur") || q.equals("chennai")
                || q.equals("goa") || q.equals("varanasi") || q.equals("agra");
    }

    // ─────────────────────────────────────────────────────────────
    // API — Step 1: resolve dest_id
    // ─────────────────────────────────────────────────────────────

    private void startSearchProcess(String city) {
        setLoading(true);
        try {
            String query = java.net.URLEncoder.encode(city, "UTF-8");
            Request request = new Request.Builder()
                    .url("https://booking-com.p.rapidapi.com/v1/hotels/locations?name="
                            + query + "&locale=en-gb")
                    .get()
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", "booking-com.p.rapidapi.com")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    showError("Connection failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 429) {
                        runOnUiThread(TourismWaysActivity.this::showQuotaError);
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONArray array = new JSONArray(response.body().string());
                            if (array.length() > 0) {
                                JSONObject loc = array.getJSONObject(0);
                                fetchFullDirectory(
                                        loc.getString("dest_id"),
                                        loc.getString("dest_type"),
                                        loc.optString("cc1", "us"));
                            } else {
                                showError("Location not found");
                            }
                        } catch (Exception e) {
                            showError("Data error: " + e.getMessage());
                        }
                    } else {
                        showError("Server error " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            showError("Request setup failed");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // API — Step 2: search hotels
    // ─────────────────────────────────────────────────────────────

    private void fetchFullDirectory(String destId, String destType, String countryCode) {
        String currency = getCurrencyFromCountry(countryCode);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        cal.add(Calendar.DAY_OF_YEAR, 14);
        String checkIn = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String checkOut = sdf.format(cal.getTime());

        String url = "https://booking-com.p.rapidapi.com/v1/hotels/search?"
                + "dest_id=" + destId
                + "&dest_type=" + destType
                + "&checkin_date=" + checkIn
                + "&checkout_date=" + checkOut
                + "&adults_number=1&room_number=1&units=metric&locale=en-gb"
                + "&filter_by_currency=" + currency
                + "&order_by=popularity";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "booking-com.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { showError("Network Error"); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 429) {
                    runOnUiThread(TourismWaysActivity.this::showQuotaError);
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    parseFullResults(response.body().string(), currency);
                } else {
                    showError("Server error: " + response.code());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Parse results
    // ─────────────────────────────────────────────────────────────

    private void parseFullResults(String json, String requestedCurrency) {
        try {
            JSONArray results = new JSONObject(json).getJSONArray("result");
            hotelList.clear();

            for (int i = 0; i < results.length(); i++) {
                JSONObject h = results.getJSONObject(i);

                String price    = h.optString("min_total_price", "");
                String photoUrl = h.optString("main_photo_url", "");
                String currency = h.optString("currencycode", requestedCurrency);
                if (currency == null || currency.isEmpty()
                        || currency.equalsIgnoreCase("null")) {
                    currency = requestedCurrency;
                }

                if (!photoUrl.isEmpty()) {
                    photoUrl = photoUrl
                            .replace("max300",  "max1280")
                            .replace("square60","max1280")
                            .replace("max500",  "max1280");
                }

                String rating = (h.has("review_score") && !h.isNull("review_score"))
                        ? h.optString("review_score", "") : "";

                // Build address
                String street = h.optString("address", "");
                String city   = h.optString("city",    "");
                String zip    = h.optString("zip",     "");
                StringBuilder addr = new StringBuilder();
                if (!street.isEmpty()) addr.append(street);
                if (!city.isEmpty())   { if (addr.length() > 0) addr.append(", ");  addr.append(city); }
                if (!zip.isEmpty())    { if (addr.length() > 0) addr.append(" — "); addr.append(zip);  }

                hotelList.add(new Hotel(
                        h.optString("hotel_id",   ""),
                        h.optString("hotel_name", "Unknown Hotel"),
                        photoUrl, price, currency, rating, addr.toString()
                ));
            }

            // Sort best → worst rating
            Collections.sort(hotelList, (a, b) -> {
                double ra = 0, rb = 0;
                try { if (a.getRating() != null && !a.getRating().isEmpty()) ra = Double.parseDouble(a.getRating()); } catch (NumberFormatException ignored) {}
                try { if (b.getRating() != null && !b.getRating().isEmpty()) rb = Double.parseDouble(b.getRating()); } catch (NumberFormatException ignored) {}
                return Double.compare(rb, ra);
            });

            hotelsFetched = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runOnUiThread(() -> {
                setLoading(false);
                adapter.notifyDataSetChanged();
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private String getCurrencyFromCountry(String cc1) {
        switch (cc1.toLowerCase(Locale.US)) {
            case "in": return "INR";
            case "gb": return "GBP";
            case "ae": return "AED";
            case "fr": case "de": case "it": case "es": return "EUR";
            default:   return "USD";
        }
    }

    private void setLoading(boolean isLoading) {
        runOnUiThread(() -> {
            if (progressBar != null)
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void showError(String msg) {
        runOnUiThread(() -> {
            setLoading(false);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void showQuotaError() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("API Limit Reached")
                .setMessage("Your daily search requests have been exhausted. "
                        + "Please try again tomorrow.")
                .setPositiveButton("OK", (d, w) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}