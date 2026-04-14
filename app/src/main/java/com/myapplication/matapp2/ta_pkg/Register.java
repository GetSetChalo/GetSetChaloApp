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
import android.widget.CheckBox;
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

public class Register extends AppCompatActivity {

    private EditText mPassword;
    private TextView mTogglePass;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ta_activity_register);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // ── If user is already logged in, go straight to HomeActivity ──
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            return;
        }


        ImageView bgWheel2 = findViewById(R.id.ta_bgWheel2);



        ObjectAnimator rotator2 = ObjectAnimator.ofFloat(bgWheel2, "rotation", 360f, 0f);
        rotator2.setDuration(30000);
        rotator2.setRepeatCount(ValueAnimator.INFINITE);
        rotator2.setInterpolator(new LinearInterpolator());
        rotator2.start();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ta_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mFullname, mEmail, mPhone;
        Button   mRegisterbtn;
        TextView mLoginbtn;
        CheckBox termsCheckbox;
        ProgressBar progressbar;

        mFullname      = findViewById(R.id.ta_fullname);
        mEmail         = findViewById(R.id.ta_email);
        mPassword      = findViewById(R.id.ta_password);
        mPhone         = findViewById(R.id.ta_phone);
        mTogglePass    = findViewById(R.id.ta_togglePasswordVisibility);
        mRegisterbtn   = findViewById(R.id.ta_loginbtn);      // "Create Account" button
        mLoginbtn      = findViewById(R.id.ta_createtext);    // "Login Here" link
        termsCheckbox  = findViewById(R.id.ta_termsCheckbox);
        progressbar    = findViewById(R.id.ta_progressBar);

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
            mPassword.setSelection(mPassword.getText().length());
        });

        mRegisterbtn.setOnClickListener(v -> {
            String fullname = mFullname.getText().toString().trim();
            String email    = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            String phone    = mPhone.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(fullname)) {
                mFullname.setError("Full name is required.");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required.");
                return;
            }
            if (password.length() < 6) {
                mPassword.setError("Password must be at least 6 characters.");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                mPhone.setError("Phone number is required.");
                return;
            }
            if (!termsCheckbox.isChecked()) {
                Toast.makeText(Register.this,
                        "Please agree to the Terms & Conditions to continue.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            progressbar.setVisibility(View.VISIBLE);

            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Step 1: The account is created! Now get their unique ID.
                                String uid = fAuth.getCurrentUser().getUid();

                                // Step 2: Bundle the specific fields from your UI for the database
                                java.util.Map<String, Object> userProfile = new java.util.HashMap<>();
                                userProfile.put("fullName", fullname);
                                userProfile.put("email", email); 
                                userProfile.put("phoneNumber", phone); // Adding the new phone field
                                userProfile.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp()); // Good practice to track when they joined

                                // Step 3: Save this bundle to Firestore using their UID
                                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(uid)
                                    .set(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        android.util.Log.d("Registration", "User profile successfully written!");
                                        Toast.makeText(Register.this,
                                                "Account created! Welcome aboard 🎉",
                                                Toast.LENGTH_SHORT).show();
                                        // ── Navigate to main app, clear auth screens ──
                                        Intent intent = new Intent(
                                                getApplicationContext(), HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        android.util.Log.w("Registration", "Error writing document", e);
                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(Register.this,
                                                "Failed to save profile: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
                            } else {
                                android.util.Log.w("Registration", "Account creation failed.", task.getException());
                                Toast.makeText(Register.this,
                                        "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
        });

        // ── "Login Here" link (bottom) ──
        mLoginbtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            overridePendingTransition(0, 0);
            finish(); // remove Register from back stack
        });

        // ── Tab switcher – Login tab ──
        TextView tabLogin = findViewById(R.id.ta_tabLogin);
        tabLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            overridePendingTransition(0, 0);
            finish();
        });

        // ── "Sign up with Google" – intentionally non‑functional ──
        // (no click listener added)
    }
}