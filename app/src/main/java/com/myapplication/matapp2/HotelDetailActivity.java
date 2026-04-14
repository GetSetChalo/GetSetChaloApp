package com.myapplication.matapp2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

// osmdroid
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class HotelDetailActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    // ── Network ────────────────────────────────────────────────────────────────
    private final OkHttpClient client = new OkHttpClient();
    private final String LOCATION_IQ_KEY = "pk.3a3525a87bcc800a67ef177857426b4a";

    // ── Map ────────────────────────────────────────────────────────────────────
    private MapView map;
    private double resolvedLat = 0.0;
    private double resolvedLon = 0.0;

    // ── Loading overlay ────────────────────────────────────────────────────
    /** Full-screen overlay shown until geocoding resolves. */
    private View mapLoadingOverlay;
    /** Guard so hideOverlay() only ever fires once. */
    private volatile boolean overlayDismissed = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ── Form state ─────────────────────────────────────────────────────────────
    private int travelerCount = 1;

    /** Raw Calendar values for the selected dates; null = not yet picked. */
    private Calendar checkInDate  = null;
    private Calendar checkOutDate = null;

    // ── Intent data ────────────────────────────────────────────────────────────
    private String hotelName;
    private String hotelPrice;
    private double basePriceOriginal = 0.0;
    private String hotelCurrency;
    private String hotelAddress;
    private String cityName;

    // ────────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // osmdroid MUST be configured before setContentView
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_hotel_detail);

        // ── Read Intent extras ─────────────────────────────────────────────────
        hotelName     = getIntent().getStringExtra("HOTEL_NAME");
        hotelPrice    = getIntent().getStringExtra("HOTEL_PRICE");
        hotelCurrency = getIntent().getStringExtra("HOTEL_CURRENCY");
        hotelAddress  = getIntent().getStringExtra("HOTEL_ADDRESS");
        cityName      = getIntent().getStringExtra("CITY_NAME");
        String hotelId = getIntent().getStringExtra("HOTEL_ID");

        if (hotelPrice != null) {
            String p = hotelPrice.contains(".") ? hotelPrice.split("\\.")[0] : hotelPrice;
            try {
                basePriceOriginal = Double.parseDouble(p);
            } catch (NumberFormatException ignored) {}
        }

        // ── Toolbar ────────────────────────────────────────────────────────────
        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(hotelName != null ? hotelName : "Hotel Details");
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // ── Address card ───────────────────────────────────────────────────────
        TextView tvAddress = findViewById(R.id.detail_address);
        TextView tvCountry = findViewById(R.id.detail_country);
        buildAddressView(tvAddress, tvCountry);

        // ── Price ──────────────────────────────────────────────────────────────
        TextView tvPrice = findViewById(R.id.detail_price);
        tvPrice.setText(formatPriceDisplay(hotelPrice, hotelCurrency));

        // ── Loading overlay ────────────────────────────────────────────────
        mapLoadingOverlay = findViewById(R.id.map_loading_overlay);

        // Safety net: hide overlay after 7 seconds regardless of geocoding result
        mainHandler.postDelayed(this::hideOverlay, 7000);

        // ── Map ────────────────────────────────────────────────────────────────
        map = findViewById(R.id.map_view);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(17.5);   // sensible default; hidden by overlay
        map.getController().setCenter(new GeoPoint(20.5937, 78.9629));
        fetchLocationAndMoveMap(hotelName, cityName != null ? cityName : "");

        // ── Form binding ───────────────────────────────────────────────────────
        setupOpenInMapsButton();
        setupRadioAppearance();
        setupStepper();
        setupDatePickers();
        setupNextButton();

        // ── Favourite button ───────────────────────────────────────────────────
        String finalHotelId      = hotelId;
        String finalHotelAddress = hotelAddress;
        android.widget.ImageButton btnFav = findViewById(R.id.btn_hotel_fav);
        FavHelper.bind(this, btnFav, "HOTEL", hotelName, cityName, finalHotelId, hotelPrice, hotelCurrency, finalHotelAddress, null);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Loading overlay
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Fades the loading overlay out over 350 ms then sets it to GONE.
     * Thread-safe: can be called from any thread; always runs on the UI thread.
     * The overlayDismissed flag ensures this only fires once.
     */
    private void hideOverlay() {
        if (overlayDismissed) return;
        overlayDismissed = true;
        // Cancel the safety-net callback if it hasn't fired yet
        mainHandler.removeCallbacksAndMessages(null);
        runOnUiThread(() -> {
            if (mapLoadingOverlay == null) return;
            mapLoadingOverlay.animate()
                    .alpha(0f)
                    .setDuration(350)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mapLoadingOverlay.setVisibility(View.GONE);
                        }
                    });
        });
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Address display helper
    // ────────────────────────────────────────────────────────────────────────────

    private void buildAddressView(TextView tvAddress, TextView tvCountry) {
        if (hotelAddress != null && !hotelAddress.isEmpty()) {
            // Split at first comma: "Street — zip" vs "City, State" portions
            String[] parts = hotelAddress.split(",\\s*", 2);
            if (parts.length == 2) {
                tvAddress.setText(parts[0].trim());
                tvCountry.setText(parts[1].trim());
            } else {
                tvAddress.setText(hotelAddress);
                tvCountry.setText(cityName != null ? cityName : "");
            }
        } else {
            tvAddress.setText(cityName != null ? cityName : "Location unavailable");
            tvCountry.setText("");
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Stepper  (± 1–10 traveler count)
    // ────────────────────────────────────────────────────────────────────────────

    private void setupStepper() {
        ImageButton btnMinus = findViewById(R.id.btn_travelers_minus);
        ImageButton btnPlus  = findViewById(R.id.btn_travelers_plus);
        TextView    tvCount  = findViewById(R.id.tv_travelers_count);

        // Render initial value
        tvCount.setText(String.format(Locale.US, "%02d", travelerCount));

        btnMinus.setOnClickListener(v -> {
            if (travelerCount > 1) {
                travelerCount--;
                tvCount.setText(String.format(Locale.US, "%02d", travelerCount));
                updateDynamicPrice();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (travelerCount < 10) {
                travelerCount++;
                tvCount.setText(String.format(Locale.US, "%02d", travelerCount));
                updateDynamicPrice();
            }
        });
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Map Intent
    // ────────────────────────────────────────────────────────────────────────────

    private void setupOpenInMapsButton() {
        MaterialButton btnOpenMaps = findViewById(R.id.btn_open_maps);
        if (btnOpenMaps != null) {
            btnOpenMaps.setOnClickListener(v -> {
                Uri gmmIntentUri;
                if (resolvedLat != 0.0 && resolvedLon != 0.0) {
                    // Open exact mapped coordinates with a label
                    gmmIntentUri = Uri.parse("geo:" + resolvedLat + "," + resolvedLon +
                            "?q=" + resolvedLat + "," + resolvedLon + "(" + Uri.encode(hotelName != null ? hotelName : "") + ")");
                } else {
                    // Fallback to text search if map isn't fully loaded yet
                    String query = hotelName != null ? hotelName : "";
                    if (cityName != null && !cityName.isEmpty()) {
                        query += ", " + cityName;
                    }
                    gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
                }

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                try {
                    startActivity(mapIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "No maps application installed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Radio-button mutual exclusion (fully manual — no RadioGroup)
    // ────────────────────────────────────────────────────────────────────────────

    private static final int[] ROOM_IDS = {
            R.id.rb_standard, R.id.rb_deluxe, R.id.rb_super_deluxe, R.id.rb_premium};
    private static final int[] AC_IDS = {
            R.id.rb_ac, R.id.rb_non_ac};

    /**
     * Wires each RadioButton in a logical group so that:
     *  • Clicking any one button checks it and unchecks all others (mutual exclusion).
     *  • The card border (bg_radio_card_selected / default) and the radio circle
     *    update on the SAME click — no double-tap required.
     *
     * Root cause of the old bugs: Android's RadioGroup cannot track RadioButtons
     * that are nested inside LinearLayout children — getCheckedRadioButtonId()
     * always returned -1 (validation failed) and mutual exclusion never fired.
     * Removing RadioGroup and driving everything from OnClickListener fixes both.
     */
    private void setupRadioAppearance() {
        wireRadioGroup(ROOM_IDS);
        wireRadioGroup(AC_IDS);
    }

    private void wireRadioGroup(int[] ids) {
        for (int targetId : ids) {
            RadioButton rb = findViewById(targetId);
            if (rb == null) continue;

            // Initialize previous state tracking
            rb.setTag(false);

            rb.setOnClickListener(v -> {
                boolean wasSelected = false;
                if (rb.getTag() != null) {
                    wasSelected = (Boolean) rb.getTag();
                }

                for (int id : ids) {
                    RadioButton btn = findViewById(id);
                    if (btn == null) continue;

                    // If this is the button we tapped, and it WASN'T selected -> select it.
                    // If it WAS selected -> deselect it. All other buttons are forced to false.
                    boolean selected = (id == targetId) && !wasSelected;

                    btn.setChecked(selected);
                    btn.setBackgroundResource(selected
                            ? R.drawable.bg_radio_card_selected
                            : R.drawable.bg_radio_card_default);
                    btn.setTag(selected);
                }
                updateDynamicPrice();
            });
        }
    }

    /** Returns true if at least one RadioButton in the given ID array is checked. */
    private boolean isAnyChecked(int[] ids) {
        for (int id : ids) {
            RadioButton rb = findViewById(id);
            if (rb != null && rb.isChecked()) return true;
        }
        return false;
    }

    /** Returns the text of the first checked RadioButton in the group, or "". */
    private String getCheckedLabel(int[] ids) {
        for (int id : ids) {
            RadioButton rb = findViewById(id);
            if (rb != null && rb.isChecked()) return rb.getText().toString();
        }
        return "";
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Date Pickers
    // ────────────────────────────────────────────────────────────────────────────

    private void setupDatePickers() {
        LinearLayout layoutCheckIn  = findViewById(R.id.layout_check_in);
        LinearLayout layoutCheckOut = findViewById(R.id.layout_check_out);
        TextView     tvCheckIn      = findViewById(R.id.tv_check_in);
        TextView     tvCheckOut     = findViewById(R.id.tv_check_out);

        layoutCheckIn.setOnClickListener(v -> showCheckInPicker(tvCheckIn, tvCheckOut));
        layoutCheckOut.setOnClickListener(v -> showCheckOutPicker(tvCheckOut));
    }

    private void showCheckInPicker(TextView tvCheckIn, TextView tvCheckOut) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    checkInDate = Calendar.getInstance();
                    checkInDate.set(year, month, dayOfMonth);

                    // Format and show
                    tvCheckIn.setText(formatDate(checkInDate));

                    // If check-out is before the new check-in, clear it
                    if (checkOutDate != null && checkOutDate.before(checkInDate)) {
                        checkOutDate = null;
                        tvCheckOut.setText("Select date");
                    }
                    updateDynamicPrice();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Constraint 1: minimum date = today (system time − 1 second for edge safety)
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void showCheckOutPicker(TextView tvCheckOut) {
        // Default calendar: tomorrow (or day after check-in if already selected)
        Calendar base = checkInDate != null ? (Calendar) checkInDate.clone() : Calendar.getInstance();
        base.add(Calendar.DAY_OF_YEAR, 1);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    checkOutDate = Calendar.getInstance();
                    checkOutDate.set(year, month, dayOfMonth);
                    tvCheckOut.setText(formatDate(checkOutDate));
                    updateDynamicPrice();
                },
                base.get(Calendar.YEAR),
                base.get(Calendar.MONTH),
                base.get(Calendar.DAY_OF_MONTH)
        );

        // Constraint 2: check-out must be strictly ON or AFTER check-in
        if (checkInDate != null) {
            // min = check-in day (start of that day, allowing same day checkout)
            Calendar minOut = (Calendar) checkInDate.clone();
            minOut.set(Calendar.HOUR_OF_DAY, 0);
            minOut.set(Calendar.MINUTE, 0);
            minOut.set(Calendar.SECOND, 0);
            minOut.set(Calendar.MILLISECOND, 0);
            dialog.getDatePicker().setMinDate(minOut.getTimeInMillis());
        } else {
            // No check-in selected yet – at minimum today
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            dialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());
        }

        dialog.show();
    }

    /** Returns a "dd/MM/yyyy" string from the given Calendar. */
    private String formatDate(Calendar cal) {
        return String.format(Locale.US, "%02d/%02d/%04d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // NEXT button – validation → BookingSummaryActivity
    // ────────────────────────────────────────────────────────────────────────────

    private void setupNextButton() {
        TextView       tvCheckIn  = findViewById(R.id.tv_check_in);
        TextView       tvCheckOut = findViewById(R.id.tv_check_out);
        MaterialButton btnNext    = findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {

            // ── Validation ─────────────────────────────────────────────────────
            String checkInStr  = tvCheckIn.getText().toString().trim();
            String checkOutStr = tvCheckOut.getText().toString().trim();

            boolean roomSelected   = isAnyChecked(ROOM_IDS);
            boolean acSelected     = isAnyChecked(AC_IDS);
            boolean checkInPicked  = !checkInStr.equals("Select date") && !checkInStr.isEmpty();
            boolean checkOutPicked = !checkOutStr.equals("Select date") && !checkOutStr.isEmpty();

            if (!roomSelected || !acSelected || !checkInPicked || !checkOutPicked) {
                Toast.makeText(this, "Please fill all parameters.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ── Resolve human-readable labels ──────────────────────────────────
            String roomType = getCheckedLabel(ROOM_IDS);
            String acStatus = getCheckedLabel(AC_IDS);

            // ── Launch BookingSummaryActivity ──────────────────────────────────
            Intent intent = new Intent(this, BookingSummaryActivity.class);
            intent.putExtra("HOTEL_NAME",     hotelName);
            intent.putExtra("HOTEL_PRICE",    hotelPrice);
            intent.putExtra("HOTEL_CURRENCY", hotelCurrency);
            intent.putExtra("HOTEL_ADDRESS",  hotelAddress);
            intent.putExtra("CITY_NAME",      cityName);
            intent.putExtra("ROOM_TYPE",      roomType);
            intent.putExtra("AC_STATUS",      acStatus);
            intent.putExtra("TRAVELERS",      travelerCount);
            intent.putExtra("CHECK_IN",       checkInStr);
            intent.putExtra("CHECK_OUT",      checkOutStr);
            startActivity(intent);
        });
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Dynamic Pricing Engine
    // ────────────────────────────────────────────────────────────────────────────

    private void updateDynamicPrice() {
        if (basePriceOriginal <= 0) return;

        // 1. Room Type
        double roomSur = 0;
        String roomType = getCheckedLabel(ROOM_IDS);
        if ("Deluxe".equals(roomType)) roomSur = 0.10 * basePriceOriginal;
        else if ("Super Deluxe".equals(roomType)) roomSur = 0.20 * basePriceOriginal;
        else if ("Premium".equals(roomType)) roomSur = 0.30 * basePriceOriginal;

        // 2. AC Status
        double acSur = 0;
        String acStatus = getCheckedLabel(AC_IDS);
        if ("AC".equals(acStatus)) acSur = 0.10 * basePriceOriginal;

        // 3. Occupancy factor
        int extraPeople = Math.max(0, travelerCount - 1);
        double occSur = (0.20 * extraPeople) * basePriceOriginal;

        double dailyRate = basePriceOriginal + roomSur + acSur + occSur;

        // 4. Days
        long days = 1;
        boolean isSameDay = false;

        if (checkInDate != null && checkOutDate != null) {
            Calendar ci = (Calendar) checkInDate.clone();
            ci.set(Calendar.HOUR_OF_DAY, 0); ci.set(Calendar.MINUTE, 0); 
            ci.set(Calendar.SECOND, 0); ci.set(Calendar.MILLISECOND, 0);

            Calendar co = (Calendar) checkOutDate.clone();
            co.set(Calendar.HOUR_OF_DAY, 0); co.set(Calendar.MINUTE, 0); 
            co.set(Calendar.SECOND, 0); co.set(Calendar.MILLISECOND, 0);

            long diffMillis = co.getTimeInMillis() - ci.getTimeInMillis();
            days = Math.round((double) diffMillis / (1000.0 * 60 * 60 * 24));

            if (days == 0) {
                isSameDay = true;
                days = 1;
            } else if (days < 0) {
                days = 1; // Safeguard
            }
        }

        double subTotal = dailyRate * days;
        double finalPrice = isSameDay ? (subTotal * 0.70) : subTotal;

        // Math.round to string to seamlessly pass back to UI
        this.hotelPrice = String.valueOf(Math.round(finalPrice));
        TextView tvPrice = findViewById(R.id.detail_price);
        tvPrice.setText(formatPriceDisplay(this.hotelPrice, hotelCurrency));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Price formatting
    // ────────────────────────────────────────────────────────────────────────────

    private String formatPriceDisplay(String price, String currencyCode) {
        if (currencyCode == null || currencyCode.isEmpty()) currencyCode = "USD";
        if (price != null && price.contains(".")) price = price.split("\\.")[0];

        String formatted;
        if (price == null || price.isEmpty() || price.equalsIgnoreCase("null")) {
            formatted = "N/A";
        } else {
            try {
                long value = Long.parseLong(price);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                DecimalFormat df = new DecimalFormat("#,##0", symbols);
                formatted = df.format(value);
            } catch (NumberFormatException e) {
                formatted = price;
            }
        }

        return currencyCode + " : " + formatted;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Geocoding (two-stage LocationIQ fallback – unchanged)
    // ────────────────────────────────────────────────────────────────────────────

    private void fetchLocationAndMoveMap(String hotelName, String city) {
        String primaryQuery = (!city.isEmpty()) ? hotelName + ", " + city : hotelName;
        performGeocode(primaryQuery, city, hotelName, 17.5);
    }

    private void performGeocode(String query, String fallbackQuery, String markerTitle, double zoom) {
        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String searchUrl = "https://us1.locationiq.com/v1/search.php?key=" + LOCATION_IQ_KEY
                    + "&q=" + encoded + "&format=json&limit=1";

            Request request = new Request.Builder().url(searchUrl).get().build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (!fallbackQuery.isEmpty() && !fallbackQuery.equals(query)) {
                        // Try city-level fallback with a closer zoom
                        performGeocode(fallbackQuery, "", markerTitle, 14.0);
                    } else {
                        // No more fallbacks – reveal the screen anyway
                        hideOverlay();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() == null) {
                        hideOverlay();
                        return;
                    }
                    String jsonResponse = response.body().string();

                    try {
                        if (jsonResponse.trim().startsWith("{")) {
                            // LocationIQ returned an error object
                            if (!fallbackQuery.isEmpty() && !fallbackQuery.equals(query)) {
                                performGeocode(fallbackQuery, "", markerTitle, 15.5);
                            } else {
                                hideOverlay();
                            }
                            return;
                        }

                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        if (jsonArray.length() > 0) {
                            JSONObject location = jsonArray.getJSONObject(0);
                            double lat = location.getDouble("lat");
                            double lon = location.getDouble("lon");

                            if (lat == 0.0 && lon == 0.0) {
                                if (!fallbackQuery.isEmpty() && !fallbackQuery.equals(query)) {
                                    performGeocode(fallbackQuery, "", markerTitle, 15.5);
                                } else {
                                    hideOverlay();
                                }
                                return;
                            }

                            runOnUiThread(() -> {
                                resolvedLat = lat;
                                resolvedLon = lon;
                                GeoPoint hotelPoint = new GeoPoint(lat, lon);
                                map.post(() -> {
                                    map.getController().setZoom(zoom);
                                    map.getController().setCenter(hotelPoint);

                                    Marker startMarker = new Marker(map);
                                    startMarker.setPosition(hotelPoint);
                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    startMarker.setTitle(markerTitle);
                                    startMarker.setIcon(getResources().getDrawable(
                                            android.R.drawable.ic_menu_mylocation, getTheme()));
                                    map.getOverlays().clear();
                                    map.getOverlays().add(startMarker);
                                    map.invalidate();

                                    // ✅ Map is rendered – reveal the screen
                                    hideOverlay();
                                });
                            });

                        } else if (!fallbackQuery.isEmpty() && !fallbackQuery.equals(query)) {
                            performGeocode(fallbackQuery, "", markerTitle, 14.0);
                        } else {
                            // Empty result, no fallback left
                            hideOverlay();
                        }

                    } catch (Exception e) {
                        if (!fallbackQuery.isEmpty() && !fallbackQuery.equals(query)) {
                            performGeocode(fallbackQuery, "", markerTitle, 14.0);
                        } else {
                            hideOverlay();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideOverlay(); // ensure overlay is removed even on setup failure
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Map lifecycle
    // ────────────────────────────────────────────────────────────────────────────

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}