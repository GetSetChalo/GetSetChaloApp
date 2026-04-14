package com.myapplication.matapp2.tourist_packages;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PackagesActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.myapplication.matapp2.R.layout.tourist_activity_packages);

        String city = getIntent().getStringExtra("city");
        if (city == null) city = "jaipur";

        String cityDisplay = city.substring(0,1).toUpperCase() + city.substring(1);
        ((TextView) findViewById(com.myapplication.matapp2.R.id.tvTitle))
                .setText("Packages in " + cityDisplay);

        List<TouristPackage> list = PackageData.getAllPackages().get(city);
        if (list != null) {
            ((TextView) findViewById(com.myapplication.matapp2.R.id.tvSubtitle))
                    .setText(list.size() + " packages available");
            RecyclerView rv = findViewById(com.myapplication.matapp2.R.id.recyclerView);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new PackageAdapter(list, city));
        }

        findViewById(com.myapplication.matapp2.R.id.btnBack)
                .setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}