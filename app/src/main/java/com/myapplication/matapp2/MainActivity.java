package com.myapplication.matapp2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    private EditText cityInput;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Set up the App Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Initialize UI Elements
        cityInput = findViewById(R.id.city);
        searchButton = findViewById(R.id.search_button);

        // 3. Handle Button Click
        searchButton.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Move to HotelListActivity and pass the city name
            // Note: HotelListActivity will show red until we create it in Step 3
            Intent intent = new Intent(MainActivity.this, TourismWaysActivity.class);
            intent.putExtra("CITY_NAME", city);
            startActivity(intent);
        });
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}