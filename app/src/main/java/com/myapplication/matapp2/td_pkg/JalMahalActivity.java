package com.myapplication.matapp2.td_pkg;

import com.myapplication.matapp2.R;
import com.myapplication.matapp2.FavHelper;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class JalMahalActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.parseColor("#0B0B1E"));
        getWindow().setNavigationBarColor(Color.parseColor("#0B0B1E"));
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        insetsController.setAppearanceLightNavigationBars(false);
        setContentView(R.layout.td_activity_jal_mahal);
        FavHelper.attachDestination(this, "Jal Mahal", "Jaipur");

        TextView tvAbout = findViewById(R.id.tvAbout);
        String aboutHtml = getString(R.string.td_jal_mahal_about_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvAbout.setText(Html.fromHtml(aboutHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvAbout.setText(Html.fromHtml(aboutHtml));
        }

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnMap = findViewById(R.id.btnOpenInMap);
        btnMap.setOnClickListener(v -> {
            String geoUri = "geo:26.9511,75.8489?q=Jal+Mahal,+Jaipur,+India";
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=Jal+Mahal,+Jaipur,+India")));
            }
        });
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}
