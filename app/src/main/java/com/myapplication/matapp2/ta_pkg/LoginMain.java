package com.myapplication.matapp2.ta_pkg;

import com.myapplication.matapp2.R;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ta_activity_main);

        // ── Rotating background wheels ──
        ImageView bgWheel  = findViewById(R.id.ta_bgWheel);
        ImageView bgWheel2 = findViewById(R.id.ta_bgWheel2);

        ObjectAnimator rotator1 = ObjectAnimator.ofFloat(bgWheel, "rotation", 0f, 360f);
        rotator1.setDuration(25000);
        rotator1.setRepeatCount(ValueAnimator.INFINITE);
        rotator1.setInterpolator(new LinearInterpolator());
        rotator1.start();

        ObjectAnimator rotator2 = ObjectAnimator.ofFloat(bgWheel2, "rotation", 360f, 0f);
        rotator2.setDuration(35000);
        rotator2.setRepeatCount(ValueAnimator.INFINITE);
        rotator2.setInterpolator(new LinearInterpolator());
        rotator2.start();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ta_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}