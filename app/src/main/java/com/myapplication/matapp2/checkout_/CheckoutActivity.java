package com.myapplication.matapp2.checkout_;

import com.myapplication.matapp2.R;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends com.myapplication.matapp2.mat_project_pkg.BaseNavActivity {

    // --- Price state ---
    private int basePrice = 7990;
    private static final double TAX_RATE = 0.18;
    private int addonTotal = 1199; // spa(899) + breakfast(300) pre-selected
    private int promoDiscount = 0;
    private TextView activePromoChip = null;

    // --- Reward Points state ---
    private int rewardPointsBalance = 300;
    private int pointsUsed = 0;
    private TextView tvAvailablePoints, tvRewardPointsApplied;
    private android.widget.ImageButton btnRewardMinus, btnRewardPlus;
    private LinearLayout rewardDiscountRow;
    private TextView tvRewardDiscountAmt;

    // --- Addon prices ---
    private static final int PRICE_SPA = 899;
    private static final int PRICE_BREAKFAST = 300;
    private static final int PRICE_TRANSFER = 650;
    private static final int PRICE_TOUR = 500;
    private static final int PRICE_ADVENTURE = 420;
    private static final int PRICE_PHOTO = 199;

    private TextView tvAddonTotal, tvSubtotal, tvTax, tvTotal, tvDiscountAmt;
    private LinearLayout discountRow;
    private TextView tvPromoHeaderSub, tvAddonHeaderSub, tvPromoBadge;
    private TextView guestChevron;
    private TextView tvHotelName, tvHotelRoomType;
    private TextView tvStayDetails, tvStayDates;

    // Guest section
    private LinearLayout guestContainer;

    // Promo section
    private LinearLayout promoBody;
    private TextView promoChevron;

    // Addon section
    private LinearLayout addonBody;
    private TextView addonChevron;

    // Addon item views
    private LinearLayout addonSpa, addonBreakfast, addonTransfer, addonTour, addonAdventure, addonPhoto;

    // Guest aadhar-attach status trackers
    private final List<TextView> aadharAttachedLabels = new ArrayList<>();

    // Number of travelers (read from intent)
    private int travelerCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity_checkout);

        // Read traveler count from the booking summary intent extras
        travelerCount = getIntent().getIntExtra("TRAVELERS", 1);
        if (travelerCount < 1) travelerCount = 1;

        // Initialize addonTotal since Spa (899) and Breakfast (300/person) are pre-selected
        addonTotal = PRICE_SPA + (PRICE_BREAKFAST * travelerCount);

        bindViews();

        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String cityName = getIntent().getStringExtra("CITY_NAME");
        String roomType = getIntent().getStringExtra("ROOM_TYPE");
        String bookingType = getIntent().getStringExtra("BOOKING_TYPE");

        // Dynamically override base price
        String hotelPriceStr = getIntent().getStringExtra("HOTEL_PRICE");
        if (hotelPriceStr != null && !hotelPriceStr.isEmpty()) {
            try {
                basePrice = Integer.parseInt(hotelPriceStr);
            } catch (Exception e) {
                basePrice = getIntent().getIntExtra("BASE_PRICE", 7990);
            }
        } else {
            basePrice = getIntent().getIntExtra("BASE_PRICE", 7990);
        }
        TextView tvPriceBaseValue = findViewById(R.id.tvPriceBaseValue);
        if (tvPriceBaseValue != null) {
            tvPriceBaseValue.setText("₹" + formatINR(basePrice));
        }

        if (hotelName != null) {
            String location = hotelName;
            if (!"PACKAGE".equals(bookingType) && cityName != null && !cityName.isEmpty()) {
                String formattedCity = cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
                location += ", " + formattedCity;
            }
            tvHotelName.setText(location);
        }
        if (roomType != null) {
            tvHotelRoomType.setText("Room Type : " + roomType);
        }

        if ("PACKAGE".equals(bookingType)) {
            TextView tvHotelLabel = findViewById(R.id.tvHotelLabel);
            if (tvHotelLabel != null) tvHotelLabel.setText("PACKAGE");
            
            if (tvHotelRoomType != null) tvHotelRoomType.setVisibility(View.GONE);

            TextView tvPriceBaseLabel = findViewById(R.id.tvPriceBaseLabel);
            if (tvPriceBaseLabel != null) {
                tvPriceBaseLabel.setText("Package (" + travelerCount + (travelerCount == 1 ? " traveller)" : " travellers)"));
            }
        }

        String checkIn = getIntent().getStringExtra("CHECK_IN");
        String checkOut = getIntent().getStringExtra("CHECK_OUT");
        String pkgDuration = getIntent().getStringExtra("DURATION_STR");

        long diffDays = 1;
        try {
            if (checkIn != null && checkOut != null && !checkIn.equals("—")) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US);
                java.util.Date inDate = sdf.parse(checkIn);
                java.util.Date outDate = sdf.parse(checkOut);
                if (inDate != null && outDate != null) {
                    long diffMillis = outDate.getTime() - inDate.getTime();
                    diffDays = java.util.concurrent.TimeUnit.DAYS.convert(diffMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String durationStr;
        if (pkgDuration != null && !pkgDuration.isEmpty()) {
            durationStr = pkgDuration;
        } else {
            if (diffDays <= 0) {
                durationStr = "1 Day";
            } else if (diffDays == 1) {
                durationStr = "1 Night, 2 Days";
            } else {
                durationStr = diffDays + " Nights, " + (diffDays + 1) + " Days";
            }
        }

        String travelerStr = travelerCount + (travelerCount == 1 ? " Traveller" : " Travellers");
        tvStayDetails.setText(travelerStr + " · " + durationStr);

        if (checkIn != null && checkOut != null && !checkIn.equals("—")) {
            tvStayDates.setText(checkIn + "  →  " + checkOut);
        } else {
            tvStayDates.setText("Select dates at property");
        }

        buildGuestForms();
        setupListeners();
        updateAddonHeader();
        updatePriceSummary();
        updateGuestHeaderSub();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────────────

    private void bindViews() {
        tvAddonTotal = findViewById(R.id.tvAddonTotal);
        tvSubtotal   = findViewById(R.id.tvSubtotal);
        tvTax        = findViewById(R.id.tvTax);
        tvTotal      = findViewById(R.id.tvTotal);
        tvDiscountAmt = findViewById(R.id.tvDiscountAmt);
        discountRow  = findViewById(R.id.discountRow);

        // Bind Reward Points Views
        tvAvailablePoints = findViewById(R.id.tvAvailablePoints);
        tvRewardPointsApplied = findViewById(R.id.tvRewardPointsApplied);
        btnRewardMinus = findViewById(R.id.btnRewardMinus);
        btnRewardPlus = findViewById(R.id.btnRewardPlus);
        rewardDiscountRow = findViewById(R.id.rewardDiscountRow);
        tvRewardDiscountAmt = findViewById(R.id.tvRewardDiscountAmt);

        // Load points from preferences
        android.content.SharedPreferences rewardPrefs = getSharedPreferences("RewardPrefs", android.content.Context.MODE_PRIVATE);
        rewardPointsBalance = rewardPrefs.getInt("points", 300);
        if (tvAvailablePoints != null) {
            tvAvailablePoints.setText("Available: " + rewardPointsBalance);
        }

        if (btnRewardMinus != null) {
            btnRewardMinus.setOnClickListener(v -> updatePointsUsed(-50));
        }
        if (btnRewardPlus != null) {
            btnRewardPlus.setOnClickListener(v -> updatePointsUsed(50));
        }
        tvPromoHeaderSub = findViewById(R.id.tvPromoHeaderSub);
        tvAddonHeaderSub = findViewById(R.id.tvAddonHeaderSub);
        tvPromoBadge = findViewById(R.id.tvPromoBadge);
        guestChevron = findViewById(R.id.guestChevron);
        guestContainer = findViewById(R.id.guestContainer);
        tvHotelName = findViewById(R.id.tvHotelName);
        tvHotelRoomType = findViewById(R.id.tvHotelRoomType);
        tvStayDetails = findViewById(R.id.tvStayDetails);
        tvStayDates = findViewById(R.id.tvStayDates);

        promoBody    = findViewById(R.id.promoBody);
        promoChevron = findViewById(R.id.promoChevron);
        addonBody    = findViewById(R.id.addonBody);
        addonChevron = findViewById(R.id.addonChevron);

        addonSpa       = findViewById(R.id.addonSpa);
        addonBreakfast = findViewById(R.id.addonBreakfast);
        addonTransfer  = findViewById(R.id.addonTransfer);
        addonTour      = findViewById(R.id.addonTour);
        addonAdventure = findViewById(R.id.addonAdventure);
        addonPhoto     = findViewById(R.id.addonPhoto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dynamically build one guest input block per traveler
    // ─────────────────────────────────────────────────────────────────────────

    private void buildGuestForms() {
        aadharAttachedLabels.clear();
        guestContainer.removeAllViews();

        for (int i = 0; i < travelerCount; i++) {
            String guestLabel = (i == 0) ? "Main Guest" : "Guest " + (i + 1);

            // ── Divider between guests ──────────────────────────────────────
            if (i > 0) {
                View divider = new View(this);
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                dp.topMargin = dpToPx(14);
                dp.bottomMargin = dpToPx(14);
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.checkout_divider));
                guestContainer.addView(divider);
            }

            // ── Guest ordinal heading ───────────────────────────────────────
            TextView tvHeading = new TextView(this);
            tvHeading.setText(guestLabel.toUpperCase());
            tvHeading.setTextColor(ContextCompat.getColor(this, R.color.checkout_gold));
            tvHeading.setTextSize(11f);
            tvHeading.setLetterSpacing(0.08f);
            tvHeading.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            headingParams.bottomMargin = dpToPx(10);
            tvHeading.setLayoutParams(headingParams);
            guestContainer.addView(tvHeading);

            // ── Full Name + Age row ─────────────────────────────────────────
            LinearLayout nameAgeRow = new LinearLayout(this);
            nameAgeRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = dpToPx(10);
            nameAgeRow.setLayoutParams(rowParams);

            // Name column
            LinearLayout nameCol = new LinearLayout(this);
            nameCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams nameColParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameColParams.setMarginEnd(dpToPx(6));
            nameCol.setLayoutParams(nameColParams);
            nameCol.addView(makeFieldLabel("FULL NAME"));
            EditText etName = makeEditText("e.g. Rahul Sharma", InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS, EditorInfo.IME_ACTION_NEXT);
            nameCol.addView(etName);
            nameAgeRow.addView(nameCol);

            // Age column
            LinearLayout ageCol = new LinearLayout(this);
            ageCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams ageColParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            ageColParams.setMarginStart(dpToPx(6));
            ageCol.setLayoutParams(ageColParams);
            ageCol.addView(makeFieldLabel("AGE"));
            EditText etAge = makeEditText("e.g. 28", InputType.TYPE_CLASS_NUMBER,
                    EditorInfo.IME_ACTION_NEXT);
            ageCol.addView(etAge);
            nameAgeRow.addView(ageCol);

            guestContainer.addView(nameAgeRow);

            // ── Contact Number ──────────────────────────────────────────────
            guestContainer.addView(makeFieldLabel("CONTACT NUMBER"));
            EditText etContact = makeEditText("e.g. +91 98765 12345",
                    InputType.TYPE_CLASS_PHONE, EditorInfo.IME_ACTION_NEXT);
            setBottomMargin(etContact, dpToPx(10));
            guestContainer.addView(etContact);

            // ── Email (main guest only shown label; all guests get it) ──────
            guestContainer.addView(makeFieldLabel("EMAIL ID"));
            EditText etEmail = makeEditText("e.g. name@mail.com",
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                    EditorInfo.IME_ACTION_NEXT);
            setBottomMargin(etEmail, dpToPx(10));
            guestContainer.addView(etEmail);

            // ── Aadhar Number ───────────────────────────────────────────────
            guestContainer.addView(makeFieldLabel("AADHAR CARD NUMBER"));
            EditText etAadhar = makeEditText("12-digit Aadhar number",
                    InputType.TYPE_CLASS_NUMBER, EditorInfo.IME_ACTION_DONE);
            setBottomMargin(etAadhar, dpToPx(10));
            guestContainer.addView(etAadhar);

            // ── Attach Aadhar PDF button ────────────────────────────────────
            guestContainer.addView(makeFieldLabel("ATTACH AADHAR (PDF)"));

            final int guestIndex = i;
            Button btnAttach = new Button(this);
            btnAttach.setText("📎  Upload Aadhar Card here");
            btnAttach.setTextColor(Color.parseColor("#F5A623"));
            btnAttach.setTextSize(13f);
            btnAttach.setBackground(ContextCompat.getDrawable(this, R.drawable.checkout_bg_attach));
            btnAttach.setStateListAnimator(null);
            LinearLayout.LayoutParams attachParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(48));
            attachParams.bottomMargin = dpToPx(4);
            btnAttach.setLayoutParams(attachParams);

            TextView tvAttached = new TextView(this);
            tvAttached.setTextColor(ContextCompat.getColor(this, R.color.checkout_green_save));
            tvAttached.setTextSize(10f);
            tvAttached.setText("✓ aadhar_guest" + (i + 1) + ".pdf attached");
            tvAttached.setVisibility(View.GONE);
            aadharAttachedLabels.add(tvAttached);

            btnAttach.setOnClickListener(v -> {
                aadharAttachedLabels.get(guestIndex).setVisibility(View.VISIBLE);
                Toast.makeText(this,
                        "aadhar_guest" + (guestIndex + 1) + ".pdf attached",
                        Toast.LENGTH_SHORT).show();
            });

            guestContainer.addView(btnAttach);
            guestContainer.addView(tvAttached);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Layout helpers
    // ─────────────────────────────────────────────────────────────────────────

    private TextView makeFieldLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.checkout_gold));
        tv.setTextSize(10f);
        tv.setAllCaps(true);
        tv.setLetterSpacing(0.05f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dpToPx(4);
        tv.setLayoutParams(p);
        return tv;
    }

    private EditText makeEditText(String hint, int inputType, int imeAction) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setHintTextColor(ContextCompat.getColor(this, R.color.checkout_text_hint));
        et.setTextColor(ContextCompat.getColor(this, R.color.checkout_text_primary));
        et.setTextSize(12f);
        et.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        et.setGravity(android.view.Gravity.CENTER_VERTICAL);
        et.setBackground(ContextCompat.getDrawable(this, R.drawable.checkout_bg_field_input));
        et.setInputType(inputType);
        et.setImeOptions(imeAction);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(40)));
        return et;
    }

    private void setBottomMargin(View v, int marginPx) {
        if (v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) v.getLayoutParams()).bottomMargin = marginPx;
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Listeners
    // ─────────────────────────────────────────────────────────────────────────

    private void setupListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Profile button
        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    this, android.R.anim.slide_in_left, android.R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        // Guest section toggle
        LinearLayout guestHeader = findViewById(R.id.guestHeader);
        guestHeader.setOnClickListener(v -> toggleSection(guestContainer, guestChevron));

        // Promo section toggle
        findViewById(R.id.promoHeader).setOnClickListener(v -> toggleSection(promoBody, promoChevron));

        // Promo chips
        TextView chipEarlyBird = findViewById(R.id.chipEarlyBird);
        TextView chipFirst50   = findViewById(R.id.chipFirst50);
        TextView chipMonsoon   = findViewById(R.id.chipMonsoon);
        TextView chipGSC200    = findViewById(R.id.chipGSC200);

        chipEarlyBird.setOnClickListener(v -> togglePromo(chipEarlyBird, "15pct",  "EARLYBIRD 15% applied"));
        chipFirst50  .setOnClickListener(v -> togglePromo(chipFirst50,   "50flat", "FIRST50 applied"));
        chipMonsoon  .setOnClickListener(v -> togglePromo(chipMonsoon,   "10pct",  "MONSOON10 applied"));
        chipGSC200   .setOnClickListener(v -> togglePromo(chipGSC200,    "200flat","GSC200 applied"));

        // Addon section toggle
        findViewById(R.id.addonHeader).setOnClickListener(v -> toggleSection(addonBody, addonChevron));

        // Addon tiles
        addonSpa      .setOnClickListener(v -> toggleAddon(addonSpa,       PRICE_SPA));
        addonBreakfast.setOnClickListener(v -> toggleAddon(addonBreakfast,  PRICE_BREAKFAST * travelerCount));
        addonTransfer .setOnClickListener(v -> toggleAddon(addonTransfer,   PRICE_TRANSFER));
        addonTour     .setOnClickListener(v -> toggleAddon(addonTour,       PRICE_TOUR));
        addonAdventure.setOnClickListener(v -> toggleAddon(addonAdventure,  PRICE_ADVENTURE));
        addonPhoto    .setOnClickListener(v -> toggleAddon(addonPhoto,      PRICE_PHOTO));

        // Main CTA
        findViewById(R.id.btnProceedConfirm).setOnClickListener(v -> showReservationDialog());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Guest header subtitle
    // ─────────────────────────────────────────────────────────────────────────

    private void updateGuestHeaderSub() {
        TextView tvSub = findViewById(R.id.guestHeaderSub);
        if (tvSub != null) {
            tvSub.setText(travelerCount + (travelerCount == 1 ? " guest · tap to fill" : " guests · tap to fill"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Section collapse / expand
    // ─────────────────────────────────────────────────────────────────────────

    private void toggleSection(LinearLayout body, TextView chevron) {
        if (body.getVisibility() == View.GONE) {
            body.setVisibility(View.VISIBLE);
            chevron.setText("⌄");
        } else {
            body.setVisibility(View.GONE);
            chevron.setText("›");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Promo toggle
    // ─────────────────────────────────────────────────────────────────────────

    private void togglePromo(TextView chip, String tag, String label) {
        int gold  = ContextCompat.getColor(this, R.color.checkout_gold);
        int white = ContextCompat.getColor(this, R.color.checkout_white);

        if (activePromoChip == chip) {
            chip.setBackgroundResource(R.drawable.checkout_bg_promo_chip);
            chip.setTextColor(gold);
            activePromoChip = null;
            promoDiscount = 0;
            tvPromoBadge.setVisibility(View.GONE);
            tvPromoHeaderSub.setText("No promo applied");
        } else {
            if (activePromoChip != null) {
                activePromoChip.setBackgroundResource(R.drawable.checkout_bg_promo_chip);
                activePromoChip.setTextColor(gold);
            }
            chip.setBackgroundResource(R.drawable.checkout_bg_promo_chip_active);
            chip.setTextColor(white);
            activePromoChip = chip;

            int base = basePrice + addonTotal;
            if (tag.endsWith("pct")) {
                int pct = Integer.parseInt(tag.replace("pct", ""));
                promoDiscount = (int) Math.round(base * pct / 100.0);
            } else {
                promoDiscount = Integer.parseInt(tag.replace("flat", ""));
            }
            tvPromoBadge.setText("✓ You save ₹" + formatINR(promoDiscount) + "!");
            tvPromoBadge.setVisibility(View.VISIBLE);
            tvPromoHeaderSub.setText(label);
        }
        updatePriceSummary();
    }

    private void recalculatePromo() {
        if (activePromoChip != null) {
            String tag = (String) activePromoChip.getTag();
            int base = basePrice + addonTotal;
            if (tag.endsWith("pct")) {
                int pct = Integer.parseInt(tag.replace("pct", ""));
                promoDiscount = (int) Math.round(base * pct / 100.0);
            } else {
                promoDiscount = Integer.parseInt(tag.replace("flat", ""));
            }
            tvPromoBadge.setText("✓ You save ₹" + formatINR(promoDiscount) + "!");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Addon toggle
    // ─────────────────────────────────────────────────────────────────────────

    private void toggleAddon(LinearLayout tile, int price) {
        String tag = (String) tile.getTag();
        if ("selected".equals(tag)) {
            tile.setTag("unselected");
            tile.setBackgroundResource(R.drawable.checkout_bg_addon);
            addonTotal -= price;
        } else {
            tile.setTag("selected");
            tile.setBackgroundResource(R.drawable.checkout_bg_addon_selected);
            addonTotal += price;
        }
        updateAddonHeader();
        recalculatePromo();
        updatePriceSummary();
    }

    private void updateAddonHeader() {
        StringBuilder sb = new StringBuilder();
        if ("selected".equals(addonSpa.getTag()))       appendComma(sb, "Spa");
        if ("selected".equals(addonBreakfast.getTag())) appendComma(sb, "Breakfast");
        if ("selected".equals(addonTransfer.getTag()))  appendComma(sb, "Transfer");
        if ("selected".equals(addonTour.getTag()))      appendComma(sb, "Tour");
        if ("selected".equals(addonAdventure.getTag())) appendComma(sb, "Adventure");
        if ("selected".equals(addonPhoto.getTag()))     appendComma(sb, "Photo");
        tvAddonHeaderSub.setText(sb.length() > 0 ? sb.toString() : "None selected");
    }

    private void appendComma(StringBuilder sb, String s) {
        if (sb.length() > 0) sb.append(" + ");
        sb.append(s);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Price calculation
    // ─────────────────────────────────────────────────────────────────────────

    private void updatePriceSummary() {
        // Enforce point limits dynamically in case addons/promos change
        int subtotalLimit = basePrice + addonTotal - promoDiscount;
        if (subtotalLimit < 0) subtotalLimit = 0;
        int maxPointsAllowed = subtotalLimit - (subtotalLimit % 50);
        if (pointsUsed > maxPointsAllowed) {
            pointsUsed = maxPointsAllowed;
            if (tvRewardPointsApplied != null) {
                tvRewardPointsApplied.setText(String.valueOf(pointsUsed));
            }
        }

        int subtotal = basePrice + addonTotal - promoDiscount - pointsUsed;
        if (subtotal < 0) subtotal = 0;
        int tax      = (int) Math.round(subtotal * TAX_RATE);
        int total    = subtotal + tax;

        tvAddonTotal.setText("₹" + formatINR(addonTotal));
        tvSubtotal  .setText("₹" + formatINR(subtotal));
        tvTax       .setText("₹" + formatINR(tax));
        tvTotal     .setText("₹" + formatINR(total));

        if (promoDiscount > 0) {
            discountRow.setVisibility(View.VISIBLE);
            tvDiscountAmt.setText("-₹" + formatINR(promoDiscount));
        } else {
            discountRow.setVisibility(View.GONE);
        }

        if (pointsUsed > 0 && rewardDiscountRow != null) {
            rewardDiscountRow.setVisibility(View.VISIBLE);
            tvRewardDiscountAmt.setText("-₹" + formatINR(pointsUsed));
        } else if (rewardDiscountRow != null) {
            rewardDiscountRow.setVisibility(View.GONE);
        }
    }

    private void updatePointsUsed(int diff) {
        int newUsed = pointsUsed + diff;
        if (newUsed < 0) newUsed = 0;
        int subtotalLimit = basePrice + addonTotal - promoDiscount;
        if (newUsed > rewardPointsBalance) newUsed = rewardPointsBalance;
        if (newUsed > subtotalLimit) newUsed = subtotalLimit - (subtotalLimit % 50);
        if (newUsed != pointsUsed) {
            pointsUsed = newUsed;
            tvRewardPointsApplied.setText(String.valueOf(pointsUsed));
            updatePriceSummary();
        }
    }

    private String formatINR(int amount) {
        return NumberFormat.getNumberInstance(new Locale("en", "IN")).format(amount);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reservation Dialog
    // ─────────────────────────────────────────────────────────────────────────

    private void showReservationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkout_dialog_reservation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.88),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        String checkInDate = getIntent().getStringExtra("CHECK_IN");
        if (checkInDate == null || checkInDate.isEmpty()) {
            checkInDate = "12/04/2026"; // Fallback
        }
        final String displayDate = checkInDate;

        TextView tvDeadline = dialog.findViewById(R.id.tvDeadline);
        tvDeadline.setText(displayDate);

        TextView tvConfirmMessage = dialog.findViewById(R.id.tvConfirmMessage);
        if (tvConfirmMessage != null) {
            String hotelName = getIntent().getStringExtra("HOTEL_NAME");
            String cityName = getIntent().getStringExtra("CITY_NAME");
            String bookingType = getIntent().getStringExtra("BOOKING_TYPE");

            if ("PACKAGE".equals(bookingType)) {
                tvConfirmMessage.setText("to confirm " + (hotelName != null ? hotelName : "your package") + ".");
            } else {
                String location = hotelName != null ? hotelName : "your hotel";
                if (cityName != null && !cityName.isEmpty()) {
                    String formattedCity = cityName.substring(0, 1).toUpperCase(Locale.US) + cityName.substring(1).toLowerCase(Locale.US);
                    location += ", " + formattedCity;
                }
                tvConfirmMessage.setText("to confirm your stay at " + location + ".");
            }
        }

        dialog.findViewById(R.id.btnPayNow).setOnClickListener(v -> {
            dialog.dismiss();
            showPaymentDialog();
        });
        dialog.findViewById(R.id.btnPayLater).setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Booking reserved! Pay before " + displayDate, Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Payment Dialog
    // ─────────────────────────────────────────────────────────────────────────

    private void showPaymentDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkout_dialog_payment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.88),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        int subtotal = basePrice + addonTotal - promoDiscount;
        int tax      = (int) Math.round(subtotal * TAX_RATE);
        int total    = subtotal + tax;
        TextView tvPayTotal = dialog.findViewById(R.id.tvPayTotal);
        tvPayTotal.setText("₹" + formatINR(total));

        Spinner spinner = dialog.findViewById(R.id.spinnerPayment);
        String[] methods = {
                "Select payment method",
                "── Credit Card ──",
                "  Visa Credit · ending 1234",
                "  Mastercard Credit · ending 5678",
                "── Debit Card ──",
                "  Visa Debit · ending 9012",
                "  RuPay Debit · ending 3456",
                "── UPI ──",
                "  Google Pay (UPI)",
                "  PhonePe (UPI)",
                "  Paytm (UPI)",
                "  Enter UPI ID"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, methods) {
            @Override public boolean isEnabled(int position) {
                return position != 1 && position != 4 && position != 7;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText etUpiId = dialog.findViewById(R.id.etUpiId);
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                etUpiId.setVisibility(pos == 11 ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });

        dialog.findViewById(R.id.btnConfirmPay).setOnClickListener(v -> {
            if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            showSuccessDialog();
        });

        dialog.findViewById(R.id.btnCancelPay).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Success Dialog
    // ─────────────────────────────────────────────────────────────────────────

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.checkout_dialog_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.82),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView tvSuccessMessage = dialog.findViewById(R.id.tvSuccessMessage);
        if (tvSuccessMessage != null) {
            String cityName = getIntent().getStringExtra("CITY_NAME");
            String hotelName = getIntent().getStringExtra("HOTEL_NAME");
            String bookingType = getIntent().getStringExtra("BOOKING_TYPE");

            String displayCity = "";
            if (cityName != null && !cityName.isEmpty()) {
                displayCity = cityName.substring(0, 1).toUpperCase(Locale.US) + cityName.substring(1).toLowerCase(Locale.US);
            }

            if ("PACKAGE".equals(bookingType)) {
                if (!displayCity.isEmpty()) {
                    tvSuccessMessage.setText("Your trip to " + displayCity + " is confirmed.\nCheck your email for the itinerary.");
                } else {
                    String pkgName = hotelName != null ? hotelName : "trip";
                    tvSuccessMessage.setText("Your " + pkgName + " is confirmed.\nCheck your email for the itinerary.");
                }
            } else {
                String hName = hotelName != null ? hotelName : "hotel stay";
                if (!displayCity.isEmpty()) {
                    tvSuccessMessage.setText("Your stay at " + hName + ", " + displayCity + " is confirmed.\nCheck your email for the booking voucher.");
                } else {
                    tvSuccessMessage.setText("Your stay at " + hName + " is confirmed.\nCheck your email for the booking voucher.");
                }
            }
            
            // SAVE TO SHAREDPREFERENCES
            saveBookingToPrefs(hotelName, cityName, bookingType);
        }

        dialog.findViewById(R.id.btnDone).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void saveBookingToPrefs(String hotelName, String cityName, String bookingType) {
        android.content.SharedPreferences prefs = getSharedPreferences("BookingsPrefs", android.content.Context.MODE_PRIVATE);
        String existingBookings = prefs.getString("BookingsArray", "[]");
        
        try {
            org.json.JSONArray array = new org.json.JSONArray(existingBookings);
            org.json.JSONObject newBooking = new org.json.JSONObject();
            
            // Recompute total cleanly for the history
            int subtotal = basePrice + addonTotal - promoDiscount;
            int total = subtotal + (int) Math.round(subtotal * TAX_RATE);
            
            String checkIn = getIntent().getStringExtra("CHECK_IN");
            String checkOut = getIntent().getStringExtra("CHECK_OUT");
            String datesFormat = (checkIn != null ? checkIn : "N/A") + " → " + (checkOut != null ? checkOut : "N/A");
            
            newBooking.put("title", hotelName != null ? hotelName : "Unknown Trip");
            newBooking.put("city", cityName != null ? cityName : "Multiple");
            newBooking.put("type", bookingType != null ? bookingType : "HOTEL");
            newBooking.put("dates", datesFormat);
            newBooking.put("total", total);
            newBooking.put("timestamp", System.currentTimeMillis());
            
            array.put(newBooking);
            prefs.edit().putString("BookingsArray", array.toString()).apply();

            // Reward Points update on checkout completion
            if (pointsUsed > 0) {
                com.myapplication.matapp2.mat_project_pkg.NavigationBarActivity.addRewardLog(this, "Booking Discount (" + (hotelName != null ? hotelName : "Booking") + ")", -pointsUsed);
            } else {
                com.myapplication.matapp2.mat_project_pkg.NavigationBarActivity.addRewardLog(this, "Booking Reward (" + (hotelName != null ? hotelName : "Booking") + ")", 50);
            }
            
            // Update local scope just in case
            android.content.SharedPreferences rewardPrefs = getSharedPreferences("RewardPrefs", android.content.Context.MODE_PRIVATE);
            rewardPointsBalance = rewardPrefs.getInt("points", 300);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getActiveTab() {
        return "explore";
    }
}