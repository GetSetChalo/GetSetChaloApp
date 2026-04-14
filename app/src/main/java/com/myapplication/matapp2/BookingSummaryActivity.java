package com.myapplication.matapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class BookingSummaryActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);

        // ── Toolbar ────────────────────────────────────────────────────────────
        MaterialToolbar toolbar = findViewById(R.id.toolbar_summary);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // ── Read Intent extras ─────────────────────────────────────────────────
        Intent intent = getIntent();
        String hotelName    = intent.getStringExtra("HOTEL_NAME");
        String hotelPrice   = intent.getStringExtra("HOTEL_PRICE");
        String hotelCurrency= intent.getStringExtra("HOTEL_CURRENCY");
        String hotelAddress = intent.getStringExtra("HOTEL_ADDRESS");
        String cityName     = intent.getStringExtra("CITY_NAME");
        String roomType     = intent.getStringExtra("ROOM_TYPE");
        String acStatus     = intent.getStringExtra("AC_STATUS");
        int    travelers    = intent.getIntExtra("TRAVELERS", 1);
        String checkIn      = intent.getStringExtra("CHECK_IN");
        String checkOut     = intent.getStringExtra("CHECK_OUT");

        // ── Sanitise nulls ─────────────────────────────────────────────────────
        if (hotelName     == null) hotelName     = "—";
        if (hotelAddress  == null) hotelAddress  = cityName != null ? cityName : "—";
        if (roomType      == null) roomType      = "—";
        if (acStatus      == null) acStatus      = "—";
        if (checkIn       == null) checkIn       = "—";
        if (checkOut      == null) checkOut      = "—";

        // Build formatted price string  e.g. "INR : 9,290"
        String priceDisplay = buildPriceString(hotelPrice, hotelCurrency);

        // ── Bind rows ──────────────────────────────────────────────────────────
        bindRow(R.id.row_hotel_name, "Hotel",        hotelName);
        bindRow(R.id.row_price,      "Price",         priceDisplay);
        bindRow(R.id.row_address,    "Address",       hotelAddress);
        bindRow(R.id.row_room_type,  "Room Type",     roomType);
        bindRow(R.id.row_ac_status,  "AC / Non-AC",   acStatus);
        bindRow(R.id.row_travelers,  "Travelers",
                travelers + (travelers == 1 ? " Guest" : " Guests"));
        bindRow(R.id.row_check_in,   "Check-In",      checkIn);
        bindRow(R.id.row_check_out,  "Check-Out",     checkOut);

        // ── Confirm button ─────────────────────────────────────────────────────
        MaterialButton btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(v -> {
            Intent checkoutIntent = new Intent(BookingSummaryActivity.this, com.myapplication.matapp2.checkout_.CheckoutActivity.class);
            if (getIntent().getExtras() != null) {
                checkoutIntent.putExtras(getIntent().getExtras());
            }
            startActivity(checkoutIntent);
        });
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Helper: bind a label + value into an included item_summary_row view
    // ────────────────────────────────────────────────────────────────────────────

    private void bindRow(int rowViewId, String label, String value) {
        View row = findViewById(rowViewId);
        if (row == null) return;
        TextView tvLabel = row.findViewById(R.id.tv_row_label);
        TextView tvValue = row.findViewById(R.id.tv_row_value);
        if (tvLabel != null) tvLabel.setText(label);
        if (tvValue != null) tvValue.setText(value);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Helper: format "INR : 9,290" from raw price string
    // ────────────────────────────────────────────────────────────────────────────

    private String buildPriceString(String price, String currency) {
        if (currency == null || currency.isEmpty()) currency = "USD";
        if (price != null && price.contains(".")) price = price.split("\\.")[0];
        if (price == null || price.isEmpty() || price.equalsIgnoreCase("null")) {
            return currency + " : N/A";
        }
        try {
            long value = Long.parseLong(price);
            java.text.DecimalFormatSymbols sym =
                    new java.text.DecimalFormatSymbols(java.util.Locale.US);
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0", sym);
            return currency + " : " + df.format(value);
        } catch (NumberFormatException e) {
            return currency + " : " + price;
        }
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}