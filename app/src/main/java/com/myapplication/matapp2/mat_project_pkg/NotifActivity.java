package com.myapplication.matapp2.mat_project_pkg;


import com.myapplication.matapp2.R;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotifActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mat_activity_notif);

        // removed "notifications");

        appName = findViewById(R.id.mat_homeAppName);

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

    }

    @Override
    protected String getActiveTab() {
        return "notifications";
    }
}