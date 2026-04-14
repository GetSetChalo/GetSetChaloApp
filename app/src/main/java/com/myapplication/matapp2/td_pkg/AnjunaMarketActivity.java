package com.myapplication.matapp2.td_pkg;
import com.myapplication.matapp2.R;
import com.myapplication.matapp2.FavHelper;

import android.content.Intent; import android.graphics.Color; import android.net.Uri; import android.os.Build; import android.os.Bundle; import android.text.Html; import android.widget.Button; import android.widget.ImageButton; import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity; import androidx.core.view.WindowCompat; import androidx.core.view.WindowInsetsControllerCompat;
public class AnjunaMarketActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.parseColor("#0B0B1E")); getWindow().setNavigationBarColor(Color.parseColor("#0B0B1E"));
        WindowInsetsControllerCompat ic = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        ic.setAppearanceLightStatusBars(false); ic.setAppearanceLightNavigationBars(false);
        setContentView(R.layout.td_activity_anjuna_market);
        FavHelper.attachDestination(this, "Anjuna Market", "Goa");
        TextView tvAbout = findViewById(R.id.tvAbout); String html = getString(R.string.td_anjuna_market_about_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) tvAbout.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)); else tvAbout.setText(Html.fromHtml(html));
        ImageButton btnBack = findViewById(R.id.btnBack); btnBack.setOnClickListener(v -> finish());
        Button btnMap = findViewById(R.id.btnOpenInMap); btnMap.setOnClickListener(v -> {
            String geo = "geo:15.5579,73.7448?q=Anjuna+Flea+Market,+Goa,+India";
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
            if (i.resolveActivity(getPackageManager()) != null) startActivity(i);
            else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=Anjuna+Flea+Market,+Goa")));
        });
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}
