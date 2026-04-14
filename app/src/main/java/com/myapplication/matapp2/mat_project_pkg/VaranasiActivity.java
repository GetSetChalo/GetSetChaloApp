package com.myapplication.matapp2.mat_project_pkg;



import com.myapplication.matapp2.R;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VaranasiActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    TextView varanasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mat_activity_varanasi);

        // removed "home");

        varanasi = findViewById(R.id.mat_textView); // ✔ correct place
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}