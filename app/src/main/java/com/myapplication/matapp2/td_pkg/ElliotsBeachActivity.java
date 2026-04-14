package com.myapplication.matapp2.td_pkg;
import com.myapplication.matapp2.R;
import com.myapplication.matapp2.FavHelper;

import android.content.Intent; import android.graphics.Color; import android.net.Uri; import android.os.Build; import android.os.Bundle; import android.text.Html; import android.widget.Button; import android.widget.ImageButton; import android.widget.TextView; import androidx.appcompat.app.AppCompatActivity; import androidx.core.view.WindowCompat; import androidx.core.view.WindowInsetsControllerCompat;
public class ElliotsBeachActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.parseColor("#0B0B1E"));
        getWindow().setNavigationBarColor(Color.parseColor("#0B0B1E"));
        WindowInsetsControllerCompat ic = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        ic.setAppearanceLightStatusBars(false); ic.setAppearanceLightNavigationBars(false);
        setContentView(R.layout.td_activity_elliots_beach);
        FavHelper.attachDestination(this, "Elliot's Beach", "Chennai");
        TextView tvAbout = findViewById(R.id.tvAbout);
        String html = getString(R.string.td_elliots_beach_about_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) tvAbout.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        else tvAbout.setText(Html.fromHtml(html));
        ImageButton btnBack = findViewById(R.id.btnBack); btnBack.setOnClickListener(v -> finish());
        Button btnMap = findViewById(R.id.btnOpenInMap); btnMap.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:12.9990,80.2720?q=Elliots+Beach+Besant+Nagar+Chennai"));
            if (i.resolveActivity(getPackageManager()) != null) startActivity(i);
            else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=Elliots+Beach+Besant+Nagar+Chennai")));
        });
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}
