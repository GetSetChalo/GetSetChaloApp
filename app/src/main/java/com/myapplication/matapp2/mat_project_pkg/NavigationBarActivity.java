package com.myapplication.matapp2.mat_project_pkg;


import com.myapplication.matapp2.R;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationBarActivity {

    // Call this from every activity's onCreate
    // Pass "home", "explore", "notifications", or "profile" as activeTab
    public static void setup(final Activity activity, String activeTab) {

        LinearLayout navHome          = activity.findViewById(R.id.mat_homeBtn);
        LinearLayout navRewards       = activity.findViewById(R.id.mat_rewardsBtn);
        LinearLayout navBookings      = activity.findViewById(R.id.mat_bookingsBtn);
        LinearLayout navProfile       = activity.findViewById(R.id.mat_profileBtn);

        TextView labelHome          = activity.findViewById(R.id.mat_labelHome);
        TextView labelRewards       = activity.findViewById(R.id.mat_labelRewards);
        TextView labelBookings      = activity.findViewById(R.id.mat_labelBookings);
        TextView labelProfile       = activity.findViewById(R.id.mat_labelProfile);

        // Reset all labels to inactive color
        int inactive = 0xFF9880B8;
        int active   = 0xFFE07818;
        labelHome.setTextColor(inactive);
        if (labelRewards != null) labelRewards.setTextColor(inactive);
        if (labelBookings != null) labelBookings.setTextColor(inactive);
        labelProfile.setTextColor(inactive);

        // Highlight the current active tab
        switch (activeTab) {
            case "home":          labelHome.setTextColor(active);          break;
            case "rewards":       if (labelRewards != null) labelRewards.setTextColor(active);       break;
            case "bookings":      if (labelBookings != null) labelBookings.setTextColor(active);      break;
            case "profile":       labelProfile.setTextColor(active);       break;
        }

        // Click listeners — each opens the right activity
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!activeTab.equals("home")) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            }
        });

        if (navRewards != null) {
            navRewards.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    showRewardPointsDialog(activity);
                }
            });
        }

        navBookings.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!activeTab.equals("bookings")) {
                    Intent intent = new Intent(activity, com.myapplication.matapp2.checkout_.MyBookingsActivity.class);
                    intent.putExtra("SOURCE_TAB", activeTab);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            }
        });

        if (navProfile != null) {
            navProfile.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (!activeTab.equals("profile")) {
                        Intent intent = new Intent(activity, com.myapplication.matapp2.checkout_.ProfileActivity.class);
                        intent.putExtra("SOURCE_TAB", activeTab);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    }
                }
            });
        }
    }

    public static void addRewardLog(android.content.Context context, String reason, int points) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("RewardPrefs", android.content.Context.MODE_PRIVATE);
        int current = prefs.getInt("points", 300);
        prefs.edit().putInt("points", current + points).apply();

        String historyJson = prefs.getString("history", "[]");
        try {
            org.json.JSONArray arr = new org.json.JSONArray(historyJson);
            org.json.JSONObject entry = new org.json.JSONObject();
            entry.put("amount", points > 0 ? "+" + points + " pts" : String.valueOf(points) + " pts");
            entry.put("reason", reason);
            entry.put("date", new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));
            arr.put(entry);
            prefs.edit().putString("history", arr.toString()).apply();
        } catch (Exception e) {}
    }

    public static void showRewardPointsDialog(final Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkout_dialog_reward_points);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.88),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        android.widget.TextView tvRewardPointsBalance = dialog.findViewById(R.id.tvRewardPointsBalance);
        if (tvRewardPointsBalance != null) {
            android.content.SharedPreferences rewardPrefs = activity.getSharedPreferences("RewardPrefs", android.content.Context.MODE_PRIVATE);
            int currentPoints = rewardPrefs.getInt("points", 300);
            tvRewardPointsBalance.setText(String.valueOf(currentPoints));
            
            LinearLayout historyContainer = dialog.findViewById(R.id.rewardHistoryContainer);
            if (historyContainer != null) {
                String historyJson = rewardPrefs.getString("history", "[]");
                try {
                    org.json.JSONArray arr = new org.json.JSONArray(historyJson);
                    // Add dummy data if empty
                    if (arr.length() == 0) {
                        String bonusDate = "Jan 01, 2026";
                        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && user.getMetadata() != null) {
                            long creationTimestamp = user.getMetadata().getCreationTimestamp();
                            bonusDate = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(new java.util.Date(creationTimestamp));
                        }
                        org.json.JSONObject d1 = new org.json.JSONObject();
                        d1.put("reason", "Welcome Bonus"); d1.put("date", bonusDate); d1.put("amount", "+300 pts");
                        arr.put(d1);
                        rewardPrefs.edit().putString("history", arr.toString()).apply();
                    }
                    
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject entry = arr.getJSONObject(i);
                        LinearLayout row = new LinearLayout(activity);
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setPadding(0, 8, 0, 8);
            
                        TextView tvRes = new TextView(activity);
                        tvRes.setText(entry.optString("reason", "Reason"));
                        tvRes.setTextColor(0xFFE0D4FF);
                        tvRes.setTextSize(13);
                        LinearLayout.LayoutParams wlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                        tvRes.setLayoutParams(wlp);
            
                        TextView tvDate = new TextView(activity);
                        tvDate.setText(entry.optString("date", "Date"));
                        tvDate.setTextColor(0xFF7A6FA0);
                        tvDate.setTextSize(11);
                        tvDate.setPadding(0, 2, 20, 0);
            
                        TextView tvPts = new TextView(activity);
                        tvPts.setText(entry.optString("amount", "+0 pts"));
                        tvPts.setTextColor(0xFF8FFFB0);
                        if (tvPts.getText().toString().startsWith("-")) {
                            tvPts.setTextColor(0xFFFF8F8F);
                        }
                        tvPts.setTextSize(13);
                        tvPts.setTypeface(null, android.graphics.Typeface.BOLD);
            
                        row.addView(tvRes);
                        row.addView(tvDate);
                        row.addView(tvPts);
                        historyContainer.addView(row);
                        
                        View div = new View(activity);
                        div.setBackgroundColor(0xFF2A1A50);
                        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                        historyContainer.addView(div);
                    }
                } catch (Exception e) {}
            }
        }

        dialog.findViewById(R.id.btnRewardClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnRewardGotIt).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
