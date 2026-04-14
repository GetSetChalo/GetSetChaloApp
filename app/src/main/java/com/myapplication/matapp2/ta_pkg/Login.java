package com.myapplication.matapp2.ta_pkg;

import com.myapplication.matapp2.R;
import com.myapplication.matapp2.mat_project_pkg.HomeActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mTogglePass;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ta_activity_login);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // ── If the user is already logged in, skip straight to HomeActivity ──
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            return;
        }

        // ── Rotating background wheels ──
        ImageView bgWheel  = findViewById(R.id.ta_bgWheel);

        ObjectAnimator rotator1 = ObjectAnimator.ofFloat(bgWheel, "rotation", 0f, 360f);
        rotator1.setDuration(20000);
        rotator1.setRepeatCount(ValueAnimator.INFINITE);
        rotator1.setInterpolator(new LinearInterpolator());
        rotator1.start();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ta_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button mLoginbtn;
        TextView mCreatebtn;
        ProgressBar progressbar;

        mEmail       = findViewById(R.id.ta_email);
        mPassword    = findViewById(R.id.ta_password);
        mTogglePass  = findViewById(R.id.ta_togglePasswordVisibility);
        progressbar  = findViewById(R.id.ta_progressBar2);
        mLoginbtn    = findViewById(R.id.ta_loginbtn);
        mCreatebtn   = findViewById(R.id.ta_createtext);

        // ── Password visibility toggle ──
        mTogglePass.setText("🙈");
        mTogglePass.setOnClickListener(v -> {
            if (!passwordVisible) {
                // Switch to Visible
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mTogglePass.setText("👁");
                passwordVisible = true;
            } else {
                // Switch to Hidden
                mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mTogglePass.setText("🙈");
                passwordVisible = false;
            }
            // Keep cursor at end of text
            mPassword.setSelection(mPassword.getText().length());
        });

        // ── Login Button ──
        mLoginbtn.setOnClickListener(v -> {
            String email    = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is Required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is Required.");
                return;
            }
            if (password.length() < 6) {
                mPassword.setError("Password must be at least 6 characters.");
                return;
            }
            progressbar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this,
                                        "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                // ── Navigate to main app, clear auth screens from back stack ──
                                Intent intent = new Intent(
                                        getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this,
                                        "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
        });

        // ── "Create Account" link (bottom) ──
        mCreatebtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
            overridePendingTransition(0, 0);
            finish(); // remove Login from back stack so pressing Back from Register exits
        });

        // ── Tab switcher – Register tab ──
        TextView tabRegister = findViewById(R.id.ta_tabRegister);
        tabRegister.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
            overridePendingTransition(0, 0);
            finish();
        });

        // ── Forgot Password – intentionally non‑functional ──
        // (no click listener added)
    }
}