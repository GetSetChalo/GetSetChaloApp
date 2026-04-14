package com.myapplication.matapp2.mat_project_pkg;


import com.myapplication.matapp2.R;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;
import java.util.HashMap;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.EditText;
import java.util.*;

public class HomeActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    EditText searchBar;
    LinearLayout searchResults;

    TextView appName;

    String[] clickableCities = {"Goa", "Varanasi", "Jaipur", "Agra", "Chennai"};
    // Store city views + names
    Map<View, String> cityMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mat_activity_home);

        // removed "home"); // ← just this one line

        searchBar = findViewById(R.id.mat_searchBar);
        searchResults = findViewById(R.id.mat_searchResults);
        appName = findViewById(R.id.mat_homeAppName);

//        // CLICKABLE CITIES
//        setClick(R.id.mat_goa, "Goa");
//        setClick(R.id.mat_varanasi, "Varanasi");
//        setClick(R.id.mat_jaipur, "Jaipur");
//        setClick(R.id.mat_agra, "Agra");
//        setClick(R.id.mat_chennai, "Chennai");

        // 1. The full string
        String fullText = "GetSetChalo";

        // 2. Create the SpannableString
        SpannableString spannable = new SpannableString(fullText);

        // 3. Define the Saffron color from your resources
        int saffronColor = getResources().getColor(R.color.mat_logo_saffron);

        // 4. Apply the color to "Set"
        // "Set" starts at index 3 and ends at index 6 (exclusive)
        spannable.setSpan(
                new ForegroundColorSpan(saffronColor),
                3,
                6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // 5. Apply the color to "Get" and "Chalo" (White)
        spannable.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0, 3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannable.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                6, 11,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // 6. Set the text to your TextView
        appName.setText(spannable);
        appName.setVisibility(View.VISIBLE);

        // 🔹 Add ALL grid cities
        addCity(R.id.mat_goa, "Goa");
        addCity(R.id.mat_varanasi, "Varanasi");
        addCity(R.id.mat_jaipur, "Jaipur");
        addCity(R.id.mat_agra, "Agra");
        addCity(R.id.mat_chennai, "Chennai");
        addCity(R.id.mat_gangtok, "Gangtok");
        addCity(R.id.mat_delhi, "Delhi");
        addCity(R.id.mat_bengaluru, "Bengaluru");
        addCity(R.id.mat_hyderabad, "Hyderabad");
        addCity(R.id.mat_kolkata, "Kolkata");
        addCity(R.id.mat_koch, "Kochi");
        addCity(R.id.mat_manali, "Manali");
        addCity(R.id.mat_ooty, "Ooty");
        addCity(R.id.mat_puri, "Puri");
        addCity(R.id.mat_amritsar, "Amritsar");
        addCity(R.id.mat_mysore, "Mysore");
        addCity(R.id.mat_tirupati, "Tirupati");
        addCity(R.id.mat_hampi, "Hampi");
        addCity(R.id.mat_shillong, "Shillong");
        addCity(R.id.mat_ahmedabad, "Ahmedabad");

        // 🔹 Add list cities
        addCity(R.id.mat_bhopal, "Bhopal");
        addCity(R.id.mat_bhubaneswar, "Bhubaneswar");
        addCity(R.id.mat_coimbatore, "Coimbatore");
        addCity(R.id.mat_cuttack, "Cuttack");
        addCity(R.id.mat_dehradun, "Dehradun");
        addCity(R.id.mat_gwalior, "Gwalior");
        addCity(R.id.mat_guntur, "Guntur");
        addCity(R.id.mat_indore, "Indore");
        addCity(R.id.mat_jodhpur, "Jodhpur");
        addCity(R.id.mat_kanpur, "Kanpur");
        addCity(R.id.mat_kolhapur, "Kolhapur");
        addCity(R.id.mat_kota, "Kota");
        addCity(R.id.mat_lucknow, "Lucknow");
        addCity(R.id.mat_ludhiana, "Ludhiana");
        addCity(R.id.mat_madurai, "Madurai");
        addCity(R.id.mat_mangalore, "Mangalore");
        addCity(R.id.mat_nagpur, "Nagpur");
        addCity(R.id.mat_nashik, "Nashik");
        addCity(R.id.mat_patna, "Patna");
        addCity(R.id.mat_pondicherry, "Pondicherry");
        addCity(R.id.mat_pune, "Pune");
        addCity(R.id.mat_raipur, "Raipur");
        addCity(R.id.mat_ranchi, "Ranchi");
        addCity(R.id.mat_surat, "Surat");
        addCity(R.id.mat_srinagar, "Srinagar");
        addCity(R.id.mat_thiruvananthapuram, "Trivandrum");
        addCity(R.id.mat_trichy, "Trichy");
        addCity(R.id.mat_udaipur, "Udaipur");
        addCity(R.id.mat_vadodara, "Vadodara");
        addCity(R.id.mat_visakhapatnam, "Vizag");

        // 🔍 Search logic
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Click listeners are now exclusively handled uniformly inside addCity() mechanism

        LinearLayout recentBtn = findViewById(R.id.mat_recentBtn);
        if (recentBtn != null) {
            recentBtn.setOnClickListener(v -> showRecentDialog());
        }

        LinearLayout favBtn = findViewById(R.id.mat_favBtn);
        if (favBtn != null) {
            favBtn.setOnClickListener(v -> showFavouritesDialog());
        }
    }


    // SEARCH FUNCTION
    void addCity(int viewId, String name) {
        View v = findViewById(viewId);
        cityMap.put(v, name.toLowerCase());

        v.setOnClickListener(view -> {
            saveRecentCityLocally(name);
            Intent intent = new Intent(this, com.myapplication.matapp2.TourismWaysActivity.class);
            intent.putExtra("CITY_NAME", name);
            startActivity(intent);
        });
    }

    private void saveRecentCityLocally(String cityName) {
        android.content.SharedPreferences prefs = getSharedPreferences("RecentlyVisitedPrefs", android.content.Context.MODE_PRIVATE);
        String savedData = prefs.getString("RecentCitiesArray", "[]");
        try {
            org.json.JSONArray array = new org.json.JSONArray(savedData);
            org.json.JSONArray newArray = new org.json.JSONArray();
            newArray.put(cityName);
            int added = 1;
            for (int i = 0; i < array.length(); i++) {
                if (added < 5 && !array.getString(i).equals(cityName)) {
                    newArray.put(array.getString(i));
                    added++;
                }
            }
            prefs.edit().putString("RecentCitiesArray", newArray.toString()).apply();
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    private void showRecentDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mat_dialog_recently_visited);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout listContainer = dialog.findViewById(R.id.recentCitiesList);
        TextView emptyState = dialog.findViewById(R.id.recentEmptyState);

        android.content.SharedPreferences prefs = getSharedPreferences("RecentlyVisitedPrefs", android.content.Context.MODE_PRIVATE);
        String savedData = prefs.getString("RecentCitiesArray", "[]");

        try {
            org.json.JSONArray array = new org.json.JSONArray(savedData);
            if (array.length() == 0) {
                emptyState.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < array.length(); i++) {
                    String cityName = array.getString(i);
                    View item = getLayoutInflater().inflate(R.layout.mat_item_recent_city, listContainer, false);
                    TextView tvName = item.findViewById(R.id.tvRecentCityName);
                    tvName.setText(cityName);
                    
                    item.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent intent = new Intent(this, com.myapplication.matapp2.TourismWaysActivity.class);
                        intent.putExtra("CITY_NAME", cityName);
                        startActivity(intent);
                    });
                    
                    listContainer.addView(item);
                }
            }
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.dialogCloseBtn).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // FAVOURITES DIALOG
    // ─────────────────────────────────────────────────────────────────────────────

    private void showFavouritesDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mat_dialog_favourites);
        dialog.getWindow().setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout listContainer = dialog.findViewById(R.id.favListContainer);
        TextView     emptyState    = dialog.findViewById(R.id.favEmptyState);

        org.json.JSONArray favourites = com.myapplication.matapp2.FavouritesManager.getAll(this);

        if (favourites.length() == 0) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < favourites.length(); i++) {
                try {
                    org.json.JSONObject item = favourites.getJSONObject(i);
                    String type = item.optString("type");
                    String name = item.optString("name");
                    String city = item.optString("city", "");

                    View row = getLayoutInflater().inflate(R.layout.mat_item_favourite, listContainer, false);
                    ((TextView) row.findViewById(R.id.tvFavType)).setText(
                            type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
                    ((TextView) row.findViewById(R.id.tvFavName)).setText(
                            name + (city.isEmpty() ? "" : "  ·  " + city));

                    final org.json.JSONObject captured = item;
                    row.setOnClickListener(v -> {
                        dialog.dismiss();
                        navigateToFavourite(captured);
                    });

                    listContainer.addView(row);
                } catch (org.json.JSONException ignored) {}
            }
        }

        dialog.findViewById(R.id.favDialogCloseBtn).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void navigateToFavourite(org.json.JSONObject item) {
        String type = item.optString("type");
        String name = item.optString("name");
        String city = item.optString("city", "");

        switch (type) {
            case "HOTEL": {
                Intent intent = new Intent(this, com.myapplication.matapp2.HotelDetailActivity.class);
                intent.putExtra("HOTEL_ID",       item.optString("hotelId"));
                intent.putExtra("HOTEL_NAME",     name);
                intent.putExtra("HOTEL_PRICE",    item.optString("hotelPrice"));
                intent.putExtra("HOTEL_CURRENCY", item.optString("hotelCurrency"));
                intent.putExtra("HOTEL_ADDRESS",  item.optString("hotelAddress"));
                intent.putExtra("CITY_NAME",      city);
                startActivity(intent);
                break;
            }
            case "PACKAGE": {
                String cityLower = city.toLowerCase(java.util.Locale.US).trim();
                java.util.List<com.myapplication.matapp2.tourist_packages.TouristPackage> list =
                        com.myapplication.matapp2.tourist_packages.PackageData.getAllPackages().get(cityLower);
                if (list != null) {
                    for (com.myapplication.matapp2.tourist_packages.TouristPackage pkg : list) {
                        if (pkg.getName().equals(name)) {
                            Intent intent = new Intent(this, com.myapplication.matapp2.tourist_packages.DetailActivity.class);
                            intent.putExtra("package",   pkg);
                            intent.putExtra("CITY_NAME", city);
                            startActivity(intent);
                            return;
                        }
                    }
                }
                android.widget.Toast.makeText(this, "Package not found", android.widget.Toast.LENGTH_SHORT).show();
                break;
            }
            case "DESTINATION": {
                String destClass = item.optString("destActivity", "");
                try {
                    Class<?> cls  = Class.forName(destClass);
                    startActivity(new Intent(this, cls));
                } catch (ClassNotFoundException e) {
                    android.widget.Toast.makeText(this, "Destination not found", android.widget.Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    void filterCities(String query) {
        query = query.toLowerCase();

        for (Map.Entry<View, String> entry : cityMap.entrySet()) {

            View view = entry.getKey();

            String city = entry.getValue();


            if (city.contains(query)) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }






    @Override
    protected String getActiveTab() {
        return "home";
    }
}