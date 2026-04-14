package com.myapplication.matapp2;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Injects a floating Favourite toggle button (top-right) into any Activity
 * without requiring changes to the layout XML.
 *
 * Usage (in onCreate, after setContentView):
 *   FavHelper.attach(this, "DESTINATION", "Hawa Mahal", "Jaipur",
 *                    "com.myapplication.matapp2.td_pkg.HawaMahalActivity");
 */
public class FavHelper {

    /** Colour constants */
    private static final int COLOR_YELLOW   = Color.parseColor("#FFC107");   // not favourited
    private static final int COLOR_HOT_PINK = Color.parseColor("#FF2D78");   // favourited

    /**
     * Attach the fav button to the activity's root decorView.
     *
     * @param activity        host activity
     * @param type            "HOTEL" | "PACKAGE" | "DESTINATION"
     * @param name            unique item name (used as key)
     * @param city            city string (used for navigation when re-opening)
     * @param extraArg        for HOTEL: JSON of extras; for PACKAGE: cityLower; for DESTINATION: activity class name
     * @param hotelId         HOTEL only; pass null for others
     * @param hotelPrice      HOTEL only; pass null for others
     * @param hotelCurrency   HOTEL only; pass null for others
     * @param hotelAddress    HOTEL only; pass null for others
     */
    public static void attach(Activity activity,
                              String type, String name, String city,
                              String hotelId, String hotelPrice,
                              String hotelCurrency, String hotelAddress,
                              String destActivityClass) {

        // Build button
        ImageButton btn = new ImageButton(activity);
        
        // Position: top-right, below status bar
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(activity, 44), dpToPx(activity, 44));
        params.gravity  = Gravity.TOP | Gravity.END;
        params.topMargin  = dpToPx(activity, 6);   // accurately centers inside a standard 56dp toolbar ((56-44)/2)
        params.rightMargin = dpToPx(activity, 16);

        FrameLayout root = activity.findViewById(android.R.id.content);
        root.addView(btn, params);

        bind(activity, btn, type, name, city, hotelId, hotelPrice, hotelCurrency, hotelAddress, destActivityClass);
    }

    public static void bind(Activity activity, ImageButton btn,
                              String type, String name, String city,
                              String hotelId, String hotelPrice,
                              String hotelCurrency, String hotelAddress,
                              String destActivityClass) {
        
        btn.setImageResource(R.drawable.mat_outline_bookmark_heart_24);
        btn.setBackground(null);
        btn.setColorFilter(FavouritesManager.isFavourite(activity, type, name)
                ? COLOR_HOT_PINK : COLOR_YELLOW);
        btn.setContentDescription("Add to Favourites");

        // Click toggle
        btn.setOnClickListener(v -> {
            boolean isFav = FavouritesManager.isFavourite(activity, type, name);
            if (isFav) {
                FavouritesManager.remove(activity, type, name);
                btn.setColorFilter(COLOR_YELLOW);
                android.widget.Toast.makeText(activity, "Removed from Favourites", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                switch (type) {
                    case "HOTEL":
                        FavouritesManager.addHotel(activity, hotelId, name, city,
                                hotelPrice, hotelCurrency, hotelAddress);
                        break;
                    case "PACKAGE":
                        FavouritesManager.addPackage(activity, name, city);
                        break;
                    case "DESTINATION":
                        FavouritesManager.addDestination(activity, name, city, destActivityClass);
                        break;
                }
                btn.setColorFilter(COLOR_HOT_PINK);
                android.widget.Toast.makeText(activity, "Added to Favourites ♥", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Convenience overloads ─────────────────────────────────────────────────

    public static void attachDestination(Activity activity, String name, String city) {
        attach(activity, "DESTINATION", name, city,
                null, null, null, null,
                activity.getClass().getName());
    }

    public static void attachPackage(Activity activity, String name, String city) {
        attach(activity, "PACKAGE", name, city,
                null, null, null, null, null);
    }

    public static void attachHotel(Activity activity, String hotelId, String name, String city,
                                   String price, String currency, String address) {
        attach(activity, "HOTEL", name, city,
                hotelId, price, currency, address, null);
    }

    // ── Util ─────────────────────────────────────────────────────────────────

    private static int dpToPx(Activity a, int dp) {
        return Math.round(dp * a.getResources().getDisplayMetrics().density);
    }
}
