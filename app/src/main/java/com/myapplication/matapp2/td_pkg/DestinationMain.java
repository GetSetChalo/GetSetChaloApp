package com.myapplication.matapp2.td_pkg;

import com.myapplication.matapp2.R;
import com.myapplication.matapp2.FavHelper;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class DestinationMain extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge + dark status bar icons
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.parseColor("#0B0B1E"));
        getWindow().setNavigationBarColor(Color.parseColor("#0B0B1E"));
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        insetsController.setAppearanceLightNavigationBars(false);

        setContentView(R.layout.td_activity_main);
        FavHelper.attachDestination(this, "Taj Mahal", "Agra");

        // ── About text with HTML bold spans ──
        TextView tvAbout = findViewById(R.id.tvAbout);
        String aboutHtml = getString(R.string.td_about_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvAbout.setText(Html.fromHtml(aboutHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvAbout.setText(Html.fromHtml(aboutHtml));
        }

        // ── Back button ──
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ── Open in Map button ──
        Button btnMap = findViewById(R.id.btnOpenInMap);
        btnMap.setOnClickListener(v -> {
            // Launch Google Maps with Taj Mahal coordinates
            String geoUri = "geo:27.1751,78.0421?q=Taj+Mahal,+Agra,+India";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback: open in browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://maps.google.com/?q=Taj+Mahal,+Agra,+India"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}
