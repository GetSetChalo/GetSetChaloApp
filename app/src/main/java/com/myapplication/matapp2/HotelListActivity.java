package com.myapplication.matapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HotelListActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout layoutComingSoon;
    private List<Hotel> hotelList = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "f94376fa4dmsh58186fe5067534dp12faecjsn2e703c521250";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        progressBar = findViewById(R.id.progressBar);
        layoutComingSoon = findViewById(R.id.layout_coming_soon);
        recyclerView = findViewById(R.id.hotel_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Extract city first so it can be passed to the adapter for map geocoding
        String city = getIntent().getStringExtra("CITY_NAME");

        adapter = new HotelAdapter(hotelList, city != null ? city : "");
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (city != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Hotels in " + city.toUpperCase(Locale.US));
            }
            if (isCitySupported(city)) {
                startSearchProcess(city);
            } else {
                recyclerView.setVisibility(View.GONE);
                layoutComingSoon.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isCitySupported(String city) {
        String query = city.toLowerCase(Locale.US).trim();
        return query.equals("jaipur") || query.equals("chennai") || 
               query.equals("goa") || query.equals("varanasi") || query.equals("agra");
    }

    private void startSearchProcess(String city) {
        setLoading(true);
        try {
            String query = java.net.URLEncoder.encode(city, "UTF-8");
            Request request = new Request.Builder()
                    .url("https://booking-com.p.rapidapi.com/v1/hotels/locations?name=" + query + "&locale=en-gb")
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
                        runOnUiThread(() -> showQuotaError());
                        return;
                    }

                    if (response.isSuccessful()) {
                        try {
                            JSONArray array = new JSONArray(response.body().string());
                            if (array.length() > 0) {
                                JSONObject location = array.getJSONObject(0);
                                String destId = location.getString("dest_id");
                                String destType = location.getString("dest_type");

                                // Extract the country code (cc1) to determine the regional currency
                                String countryCode = location.optString("cc1", "us");

                                fetchFullDirectory(destId, destType, countryCode);
                            } else {
                                showError("Location not found");
                            }
                        } catch (Exception e) {
                            showError("Data Error: " + e.getMessage());
                        }
                    } else {
                        showError("Server Error " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            showError("Request setup failed");
        }
    }

    private void fetchFullDirectory(String destId, String destType, String countryCode) {
        // Map country code to currency (mimicking your JSON mapping logic)
        String currency = getCurrencyFromCountry(countryCode);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        cal.add(Calendar.DAY_OF_YEAR, 14);
        String checkIn = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String checkOut = sdf.format(cal.getTime());

        // filter_by_currency is dynamic (e.g., INR for India, GBP for London)
        String url = "https://booking-com.p.rapidapi.com/v1/hotels/search?" +
                "dest_id=" + destId +
                "&dest_type=" + destType +
                "&checkin_date=" + checkIn +
                "&checkout_date=" + checkOut +
                "&adults_number=1&room_number=1&units=metric&locale=en-gb" +
                "&filter_by_currency=" + currency +
                "&order_by=popularity";

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
                    runOnUiThread(() -> showQuotaError());
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    // FIX: Pass the dynamic 'currency' variable down to the parser!
                    parseFullResults(response.body().string(), currency);
                } else {
                    showError("Server Error: " + response.code());
                }
            }
        });
    }

    private String getCurrencyFromCountry(String cc1) {
        switch (cc1.toLowerCase()) {
            case "in": return "INR";
            case "gb": return "GBP";
            case "us": return "USD";
            case "ae": return "AED";
            case "fr": case "de": case "it": case "es": return "EUR";
            default: return "USD";
        }
    }

    // FIX: Added 'requestedCurrency' parameter to act as a foolproof fallback
    private void parseFullResults(String json, String requestedCurrency) {
        try {
            JSONObject data = new JSONObject(json);
            JSONArray results = data.getJSONArray("result");
            hotelList.clear();

            for (int i = 0; i < results.length(); i++) {
                JSONObject hotelObj = results.getJSONObject(i);

                String price = hotelObj.optString("min_total_price", "");
                String url = hotelObj.optString("main_photo_url", "");

                // FIX: Check proper API keys ("currencycode"), but fallback to requestedCurrency if not found.
                String currency = hotelObj.optString("currencycode", requestedCurrency);

                if (currency == null || currency.isEmpty() || currency.equalsIgnoreCase("null")) {
                    currency = requestedCurrency;
                }

                if (!url.isEmpty()) {
                    url = url.replace("max300", "max1280")
                            .replace("square60", "max1280")
                            .replace("max500", "max1280");
                }

                // Extract review_score; the API may return it as a number or string
                String rating = "";
                if (hotelObj.has("review_score") && !hotelObj.isNull("review_score")) {
                    rating = hotelObj.optString("review_score", "");
                }

                // Build a human-readable address string from the API fields
                String street = hotelObj.optString("address", "");
                String cityName = hotelObj.optString("city", "");
                String zip = hotelObj.optString("zip", "");
                StringBuilder addressBuilder = new StringBuilder();
                if (!street.isEmpty()) addressBuilder.append(street);
                if (!cityName.isEmpty()) {
                    if (addressBuilder.length() > 0) addressBuilder.append(", ");
                    addressBuilder.append(cityName);
                }
                if (!zip.isEmpty()) {
                    if (addressBuilder.length() > 0) addressBuilder.append(" — ");
                    addressBuilder.append(zip);
                }
                String address = addressBuilder.toString();

                hotelList.add(new Hotel(
                        hotelObj.optString("hotel_id", ""),
                        hotelObj.optString("hotel_name", "Unknown Hotel"),
                        url,
                        price,
                        currency,
                        rating,
                        address
                ));
            }
            
            // Sort from best rating to worst rating
            java.util.Collections.sort(hotelList, (h1, h2) -> {
                double r1 = 0.0;
                double r2 = 0.0;
                try {
                    if (h1.getRating() != null && !h1.getRating().isEmpty()) {
                        r1 = Double.parseDouble(h1.getRating());
                    }
                } catch (NumberFormatException ignored) {}
                try {
                    if (h2.getRating() != null && !h2.getRating().isEmpty()) {
                        r2 = Double.parseDouble(h2.getRating());
                    }
                } catch (NumberFormatException ignored) {}
                return Double.compare(r2, r1);
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runOnUiThread(() -> {
                setLoading(false);
                adapter.notifyDataSetChanged();
            });
        }
    }

    private void showQuotaError() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("API Limit Reached")
                .setMessage("Your daily search requests have been exhausted. Please try again tomorrow.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void setLoading(final boolean isLoading) {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showError(final String msg) {
        runOnUiThread(() -> {
            setLoading(false);
            Toast.makeText(HotelListActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}