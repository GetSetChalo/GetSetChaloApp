package com.myapplication.matapp2.mat_project_pkg;


import com.myapplication.matapp2.R;
import com.myapplication.matapp2.ta_pkg.Login;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ImageView logo, mandalaView; // 1. Added mandalaView here

    TextView appName, tagline;
    LinearLayout dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.mat_activity_splash);

        // 🔗 Connect views
        logo = findViewById(R.id.mat_splashLogo);
        mandalaView = findViewById(R.id.mat_mandala_view); // 2. Connect the mandala view
        appName = findViewById(R.id.mat_splashAppName);
        tagline = findViewById(R.id.mat_splashTagline);
        dots = findViewById(R.id.mat_loadingDots);

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

        appName.setText(spannable);

// 👇 ADD THIS HERE
        appName.setAlpha(0f);
        appName.setVisibility(View.VISIBLE);

        appName.animate()
                .alpha(1f)
                .setDuration(2000)
                .start();

        // 🎡 MANDALA ROTATION ANIMATION
        // This starts immediately when the screen opens
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(20000);
        rotate.setRepeatCount(Animation.INFINITE);

// CRITICAL: LinearInterpolator ensures there is NO pause or slow-down
// when the circle finishes 360 degrees and starts again at 0.
        rotate.setInterpolator(new LinearInterpolator());

        mandalaView.startAnimation(rotate);

        // 🔥 ANIMATION 1 — Logo Fade + Scale
        Animation scale = new ScaleAnimation(
                0.8f, 1f,
                0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(800);
        scale.setInterpolator(new DecelerateInterpolator());

        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        logo.startAnimation(scale);
        logo.startAnimation(fadeIn);
        logo.setVisibility(View.VISIBLE);

        // 🌟 Glow fade in
        Animation glowFade = new AlphaAnimation(0f, 1f);
        glowFade.setDuration(1000);


        // 📝 Text fade in (delayed)
        new Handler().postDelayed(() -> {
            appName.startAnimation(new AlphaAnimation(0f, 1f));
            tagline.startAnimation(new AlphaAnimation(0f, 1f));
            appName.setVisibility(View.VISIBLE);
            tagline.setVisibility(View.VISIBLE);
        }, 500);

        // 🔵 Dots animation
        new Handler().postDelayed(() -> {
            dots.setVisibility(View.VISIBLE);
            animateDots();
        }, 900);

        // 🚀 After splash: ALWAYS route to Login screen on cold start
        //    By signing out here, we ensure a fresh cold start requires a new login.
        //    Foreground resume → SplashActivity is already finished (not in back stack),
        //    so Android automatically restores the user's last active screen.
        new Handler().postDelayed(() -> {
            // Clear any lingering session from previous app runs
            FirebaseAuth.getInstance().signOut();
            
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000);
    }

    private void animateDots() {
        View dot1 = findViewById(R.id.mat_dot1);
        View dot2 = findViewById(R.id.mat_dot2);
        View dot3 = findViewById(R.id.mat_dot3);

        // Run the pulses
        runSinglePulse(dot1, 0);
        runSinglePulse(dot2, 400);
        runSinglePulse(dot3, 800);

        // --- CHANGE THIS LINE ---
        // 1600ms (Total motion) + 400ms (Your gap) = 2000ms
        new Handler().postDelayed(this::animateDots, 2000);
    }

    private void runSinglePulse(View dot, long delay) {
        // Scale up and down
        ScaleAnimation scale = new ScaleAnimation(
                1f, 1.4f, 1f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        // Fade in and out
        AlphaAnimation alpha = new AlphaAnimation(0.3f, 1.0f);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scale);
        set.addAnimation(alpha);

        set.setDuration(500);              // 0.5s to grow
        set.setStartOffset(delay);         // Initial delay for the wave
        set.setRepeatMode(Animation.REVERSE);
        set.setRepeatCount(1);             // Plays ONCE (up then down)
        set.setInterpolator(new AccelerateDecelerateInterpolator());

        // Apply Saffron color
        dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(R.color.mat_logo_saffron)
        ));

        dot.startAnimation(set);
    }
}