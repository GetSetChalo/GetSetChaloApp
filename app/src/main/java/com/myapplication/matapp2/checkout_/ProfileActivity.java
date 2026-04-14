package com.myapplication.matapp2.checkout_;

import com.myapplication.matapp2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    private static final String TAG = "CheckoutProfileActivity";
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1001;

    // ── Firebase ──────────────────────────────────────────────────────────────
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // ── UI ────────────────────────────────────────────────────────────────────
    private TextView tvProfileInitial;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private LinearLayout rowPersonalDetails;

    // ── Cached Firestore data ─────────────────────────────────────────────────
    private String cachedFullName = "";
    private String cachedEmail    = "";
    private String cachedPhone    = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_profile);

        // ── Firebase init ──────────────────────────────────────────────────
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // ── Bind views ─────────────────────────────────────────────────────
        tvProfileInitial  = findViewById(R.id.tvProfileInitial);
        tvProfileName     = findViewById(R.id.tvProfileName);
        tvProfileEmail    = findViewById(R.id.tvProfileEmail);
        rowPersonalDetails = findViewById(R.id.rowPersonalDetails);

        // ── Back button ────────────────────────────────────────────────────
        findViewById(R.id.btnProfileBack).setOnClickListener(v -> finish());

        // ── Logout button ──────────────────────────────────────────────────
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, com.myapplication.matapp2.ta_pkg.Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // ── Personal Details click ─────────────────────────────────────────
        rowPersonalDetails.setOnClickListener(v -> showPersonalDetailsDialog());

        // ── Reward Points row ──────────────────────────────────────────────
        LinearLayout rowRewardPoints = findViewById(R.id.rowRewardPoints);
        if (rowRewardPoints != null) {
            // Update reward points badge from SharedPreferences
            android.content.SharedPreferences rewardPrefs =
                    getSharedPreferences("RewardPrefs", android.content.Context.MODE_PRIVATE);
            int currentPoints = rewardPrefs.getInt("points", 300);
            TextView tvRewardPoints = findViewById(R.id.tvProfileRewardPoints);
            if (tvRewardPoints != null) {
                tvRewardPoints.setText(currentPoints + " pts");
            }
            rowRewardPoints.setOnClickListener(v -> com.myapplication.matapp2.mat_project_pkg.NavigationBarActivity.showRewardPointsDialog(ProfileActivity.this));
        }



        View rowPasswordSecurity = findViewById(R.id.rowPasswordSecurity);
        if (rowPasswordSecurity != null) rowPasswordSecurity.setOnClickListener(v -> authenticateThenOpenSecurity());

        View rowHelpSupport = findViewById(R.id.rowHelpSupport);
        if (rowHelpSupport != null) rowHelpSupport.setOnClickListener(v -> showHelpSupportDialog());

        View rowPrivacyPolicy = findViewById(R.id.rowPrivacyPolicy);
        if (rowPrivacyPolicy != null) rowPrivacyPolicy.setOnClickListener(v -> showPrivacyPolicyDialog());

        // ── Preferences Switches ───────────────────────────────────────────
        CompoundButton switchNotif = findViewById(R.id.switchNotif);
        if (switchNotif != null) {
            switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String msg = isChecked ? "Notifications turned ON" : "Notifications turned OFF";
                new AlertDialog.Builder(this)
                        .setTitle("Notifications")
                        .setMessage(msg)
                        .setPositiveButton("OK", null)
                        .show();
            });
        }
        
        CompoundButton switchDarkMode = findViewById(R.id.switchDarkMode);
        CompoundButton switchBiometric = findViewById(R.id.switchBiometric);

        // ── Load profile from Firestore ────────────────────────────────────
        loadUserProfile();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            tvProfileName.setText("Guest");
            tvProfileInitial.setText("G");
            return;
        }

        String uid = user.getUid();

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener((DocumentSnapshot doc) -> {
                if (doc.exists()) {
                    cachedFullName = doc.getString("fullName");
                    cachedEmail    = doc.getString("email");
                    cachedPhone    = doc.getString("phoneNumber");

                    if (cachedFullName == null) cachedFullName = "";
                    if (cachedEmail    == null) cachedEmail    = user.getEmail() != null ? user.getEmail() : "";
                    if (cachedPhone    == null) cachedPhone    = "";
                } else {
                    // Fallback to Firebase Auth data if Firestore doc is missing
                    cachedFullName = user.getDisplayName() != null ? user.getDisplayName() : "";
                    cachedEmail    = user.getEmail() != null ? user.getEmail() : "";
                    cachedPhone    = "";
                    Log.w(TAG, "No Firestore doc for uid: " + uid);
                }

                // Update the UI
                tvProfileName.setText(cachedFullName.isEmpty() ? "Traveller" : cachedFullName);
                tvProfileEmail.setText(cachedEmail);

                // Set first letter initial
                if (!cachedFullName.isEmpty()) {
                    tvProfileInitial.setText(
                        String.valueOf(cachedFullName.charAt(0)).toUpperCase());
                } else {
                    tvProfileInitial.setText("T");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to load profile", e);
                Toast.makeText(this, "Could not load profile data.", Toast.LENGTH_SHORT).show();
                tvProfileName.setText("—");
                tvProfileInitial.setText("?");
            });
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void showPersonalDetailsDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));

        FirebaseUser user = mAuth.getCurrentUser();
        String dojStr = "N/A";
        if (user != null && user.getMetadata() != null) {
            long creationTimestamp = user.getMetadata().getCreationTimestamp();
            dojStr = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date(creationTimestamp));
        }

        android.content.SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedAge = userPrefs.getString("personalAge", "");
        String savedDob = userPrefs.getString("personalDob", "");
        String recEmail = userPrefs.getString("recoveryEmail", "N/A");
        String recPhone = userPrefs.getString("recoveryPhone", "N/A");

        final android.widget.EditText etName = field("Full Name", cachedFullName.isEmpty() ? "Riya Sharma" : cachedFullName,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        final android.widget.EditText etAge  = field("Age", savedAge,
                InputType.TYPE_CLASS_NUMBER);
        final android.widget.EditText etDob  = field("Date of Birth (DD/MM/YYYY)", savedDob,
                InputType.TYPE_CLASS_TEXT);
        final android.widget.EditText etDoj  = field("Date of Joining App", dojStr,
                InputType.TYPE_CLASS_TEXT);
        etDoj.setEnabled(false);
        etDoj.setAlpha(0.5f);
        
        final android.widget.EditText etContactNo = field("Contact No.", cachedPhone.isEmpty() ? "N/A" : cachedPhone, InputType.TYPE_CLASS_PHONE);
        
        final android.widget.EditText etRecEmail = field("Recovery Email", recEmail, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etRecEmail.setEnabled(false);
        etRecEmail.setAlpha(0.5f);

        final android.widget.EditText etRecPhone = field("Recovery Contact Number", recPhone, InputType.TYPE_CLASS_PHONE);
        etRecPhone.setEnabled(false);
        etRecPhone.setAlpha(0.5f);

        layout.addView(label("FULL NAME"));      layout.addView(etName);
        layout.addView(label("AGE"));            layout.addView(etAge);
        layout.addView(label("DATE OF BIRTH")); layout.addView(etDob);
        layout.addView(label("DATE OF JOINING (read-only)")); layout.addView(etDoj);
        layout.addView(label("CONTACT NO.")); layout.addView(etContactNo);
        layout.addView(label("RECOVERY EMAIL (read-only)")); layout.addView(etRecEmail);
        layout.addView(label("RECOVERY CONTACT NUMBER (read-only)")); layout.addView(etRecPhone);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.addView(layout);

        new AlertDialog.Builder(this)
                .setTitle("Personal Details")
                .setView(scrollView)
                .setPositiveButton("Save", (d, w) -> {
                        userPrefs.edit()
                                 .putString("personalAge", etAge.getText().toString())
                                 .putString("personalDob", etDob.getText().toString())
                                 .apply();
                        Toast.makeText(this, "Personal details saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    // =============================================================
    // 2. CONTACT & EMAIL
    // =============================================================
    private void showContactEmailDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));

        final android.widget.EditText etPhone    = field("Mobile Number", cachedPhone.isEmpty() ? "+91 98765 43210" : cachedPhone, InputType.TYPE_CLASS_PHONE);
        final android.widget.EditText etEmail    = field("Email ID", cachedEmail.isEmpty() ? "riya.sharma@gmail.com" : cachedEmail,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final android.widget.EditText etRecPhone = field("Recovery Phone", "", InputType.TYPE_CLASS_PHONE);
        final android.widget.EditText etRecEmail = field("Recovery Email", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        layout.addView(label("MOBILE NUMBER"));        layout.addView(etPhone);
        layout.addView(label("EMAIL ID"));             layout.addView(etEmail);
        layout.addView(label("RECOVERY PHONE"));       layout.addView(etRecPhone);
        layout.addView(label("RECOVERY EMAIL"));       layout.addView(etRecEmail);

        new AlertDialog.Builder(this)
                .setTitle("Contact and Email")
                .setView(layout)
                .setPositiveButton("Save", (d, w) ->
                        Toast.makeText(this, "Contact details saved", Toast.LENGTH_SHORT).show())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // =============================================================
    // 3. MY BOOKINGS
    // =============================================================
    private void showMyBookingsDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(16), dp(16), dp(8));

        String[][] trips = {
                {"Agra  -  Taj Mahal Heritage Tour",  "12 Apr 2026  -  15 Apr 2026", "Completed"},
                {"Varanasi  -  Ganga Aarti Experience","20 Jan 2026  -  23 Jan 2026", "Completed"},
                {"Jaipur  -  Pink City Tour",          "05 Nov 2025  -  08 Nov 2025", "Completed"}
        };

        for (String[] trip : trips) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.checkout_bg_card);
            card.setPadding(dp(14), dp(12), dp(14), dp(12));
            LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            clp.setMargins(0, 0, 0, dp(10));
            card.setLayoutParams(clp);

            TextView tvName = new TextView(this);
            tvName.setText(trip[0]);
            tvName.setTextColor(0xFFE0D4FF);
            tvName.setTextSize(13);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvDates = new TextView(this);
            tvDates.setText(trip[1]);
            tvDates.setTextColor(0xFF7A6FA0);
            tvDates.setTextSize(11);
            tvDates.setPadding(0, dp(3), 0, 0);

            TextView tvStatus = new TextView(this);
            tvStatus.setText("Status: " + trip[2]);
            tvStatus.setTextColor(0xFF8FFFB0);
            tvStatus.setTextSize(11);
            tvStatus.setPadding(0, dp(3), 0, 0);

            card.addView(tvName);
            card.addView(tvDates);
            card.addView(tvStatus);
            layout.addView(card);
        }

        new AlertDialog.Builder(this)
                .setTitle("My Bookings  (3 trips)")
                .setView(layout)
                .setPositiveButton("Close", null)
                .show();
    }

    // =============================================================
    // 4. REWARDS & POINTS
    // =============================================================
    // showRewardsDialog() removed due to centralized NavigationBarActivity points dialog.

    // =============================================================
    // 7. PASSWORD & SECURITY
    // =============================================================
    private void authenticateThenOpenSecurity() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km != null && km.isKeyguardSecure()) {
            Intent intent = km.createConfirmDeviceCredentialIntent(
                    "Authentication Required",
                    "Confirm your screen lock to access security settings");
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
                return;
            }
        }
        // No lock set on device — open directly
        openSecurityOptions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                openSecurityOptions();
            } else {
                Toast.makeText(this, "Authentication failed. Access denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openSecurityOptions() {
        final String[] options = {
                "Change Password",
                "Edit Recovery Email",
                "Edit Recovery Phone",
                "Delete Account"
        };
        new AlertDialog.Builder(this)
                .setTitle("Password and Security")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: showChangePasswordDialog(); break;
                        case 1: showEditRecoveryEmailDialog(); break;
                        case 2: showEditRecoveryPhoneDialog(); break;
                        case 3: showDeleteAccountConfirm(); break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));

        final android.widget.EditText etCurrent = field("Current Password", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final android.widget.EditText etNew     = field("New Password", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final android.widget.EditText etConfirm = field("Confirm New Password", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(label("CURRENT PASSWORD")); layout.addView(etCurrent);
        layout.addView(label("NEW PASSWORD"));     layout.addView(etNew);
        layout.addView(label("CONFIRM PASSWORD")); layout.addView(etConfirm);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(layout)
                .setPositiveButton("Update", (d, w) -> {
                    String newPass    = etNew.getText().toString();
                    String confirmPass = etConfirm.getText().toString();
                    if (newPass.isEmpty()) {
                        Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (newPass.length() < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else if (!newPass.equals(confirmPass)) {
                        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.updatePassword(newPass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditRecoveryEmailDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));
        final android.widget.EditText et = field("Recovery Email", "riya.backup@gmail.com",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(label("RECOVERY EMAIL"));
        layout.addView(et);
        new AlertDialog.Builder(this)
                .setTitle("Edit Recovery Email")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                        android.content.SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        userPrefs.edit().putString("recoveryEmail", et.getText().toString()).apply();
                        Toast.makeText(this, "Recovery email updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditRecoveryPhoneDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(16), dp(20), dp(8));
        final android.widget.EditText et = field("Recovery Phone", "+91 91234 56789",
                InputType.TYPE_CLASS_PHONE);
        layout.addView(label("RECOVERY PHONE"));
        layout.addView(et);
        new AlertDialog.Builder(this)
                .setTitle("Edit Recovery Phone")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                        android.content.SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        userPrefs.edit().putString("recoveryPhone", et.getText().toString()).apply();
                        Toast.makeText(this, "Recovery phone updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAccountConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply();
                                mAuth.signOut();
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, com.myapplication.matapp2.ta_pkg.Register.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showHelpSupportDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Help and Support")
                .setMessage("For any issues or queries, please reach out to us:\n\n" +
                        "Email:  support@getsetchalo.in\n\n" +
                        "Phone:  +91 98001 23456\n\n" +
                        "Our support team is available Monday to Saturday, 9 AM to 6 PM IST.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacyPolicyDialog() {
        android.text.SpannableString message = new android.text.SpannableString(
                "By using GetSetChalo you agree to our terms of data usage and privacy practices.\n\n" +
                        "Click here to read the full Privacy Policy:\n\n" +
                        "https://www.getsetchalo.in/privacy-policy");

        // Make the URL clickable
        int start = message.toString().indexOf("https://");
        int end   = message.length();
        message.setSpan(
                new android.text.style.URLSpan("https://www.getsetchalo.in/privacy-policy"),
                start, end,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage(message)
                .setPositiveButton("Close", null)
                .create();
        dialog.show();

        // Make the links actually clickable inside the dialog
        android.widget.TextView tvMsg = dialog.findViewById(android.R.id.message);
        if (tvMsg != null) {
            tvMsg.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        }
    }

    // =============================================================
    // HELPER METHODS
    // =============================================================
    private android.widget.EditText field(String hint, String prefill, int inputType) {
        android.widget.EditText et = new android.widget.EditText(this);
        et.setHint(hint);
        et.setText(prefill);
        et.setInputType(inputType);
        et.setTextColor(0xFFE0D4FF);
        et.setHintTextColor(0xFF4A3D6A);
        et.setTextSize(13);
        // Fallback to simple color if bg_field_input is unavailable.
        // We will try to set it but wrap in try-catch just in case the checkout project lacks this drawable.
        try {
            int resId = getResources().getIdentifier("checkout_bg_field_input", "drawable", getPackageName());
            if (resId != 0) et.setBackgroundResource(resId);
            else et.setBackgroundColor(0xFF2A1A50);
        } catch (Exception e) {
            et.setBackgroundColor(0xFF2A1A50);
        }
        et.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        lp.setMargins(0, 0, 0, dp(12));
        et.setLayoutParams(lp);
        return et;
    }

    private TextView label(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFFF5A623);
        tv.setTextSize(10);
        tv.setAllCaps(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(4));
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    @Override
    protected String getActiveTab() {
        return "profile";
    }
}