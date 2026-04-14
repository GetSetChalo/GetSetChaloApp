package com.myapplication.matapp2;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Central manager for storing and reading Favourites data via SharedPreferences.
 *
 * JSON structure per item:
 * {
 *   "type": "HOTEL" | "PACKAGE" | "DESTINATION",
 *   "name": "...",
 *   "city": "...",
 *   "hotelId":       "..." (HOTEL only),
 *   "hotelPrice":    "..." (HOTEL only),
 *   "hotelCurrency": "..." (HOTEL only),
 *   "hotelAddress":  "..." (HOTEL only),
 *   "destActivity":  "com.example.HawaMahalActivity" (DESTINATION only)
 * }
 */
public class FavouritesManager {

    private static final String PREFS_NAME = "FavouritesPrefs";
    private static final String KEY_ARRAY  = "FavouritesArray";

    // ── Public read helpers ───────────────────────────────────────────────────

    public static JSONArray getAll(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        try {
            return new JSONArray(prefs.getString(KEY_ARRAY, "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public static boolean isFavourite(Context ctx, String type, String name) {
        try {
            JSONArray arr = getAll(ctx);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optString("type").equals(type) && o.optString("name").equals(name)) {
                    return true;
                }
            }
        } catch (JSONException ignored) {}
        return false;
    }

    // ── Add helpers ───────────────────────────────────────────────────────────

    public static void addHotel(Context ctx, String hotelId, String name, String city,
                                String price, String currency, String address) {
        if (isFavourite(ctx, "HOTEL", name)) return;
        try {
            JSONObject o = new JSONObject();
            o.put("type",          "HOTEL");
            o.put("name",          name);
            o.put("city",          city);
            o.put("hotelId",       hotelId != null ? hotelId : "");
            o.put("hotelPrice",    price    != null ? price    : "");
            o.put("hotelCurrency", currency != null ? currency : "INR");
            o.put("hotelAddress",  address  != null ? address  : "");
            save(ctx, getAll(ctx).put(o));
        } catch (JSONException ignored) {}
    }

    public static void addPackage(Context ctx, String name, String city) {
        if (isFavourite(ctx, "PACKAGE", name)) return;
        try {
            JSONObject o = new JSONObject();
            o.put("type", "PACKAGE");
            o.put("name", name);
            o.put("city", city != null ? city : "");
            save(ctx, getAll(ctx).put(o));
        } catch (JSONException ignored) {}
    }

    public static void addDestination(Context ctx, String name, String city, String activityClass) {
        if (isFavourite(ctx, "DESTINATION", name)) return;
        try {
            JSONObject o = new JSONObject();
            o.put("type",         "DESTINATION");
            o.put("name",         name);
            o.put("city",         city != null ? city : "");
            o.put("destActivity", activityClass);
            save(ctx, getAll(ctx).put(o));
        } catch (JSONException ignored) {}
    }

    // ── Remove ────────────────────────────────────────────────────────────────

    public static void remove(Context ctx, String type, String name) {
        try {
            JSONArray old = getAll(ctx);
            JSONArray neu = new JSONArray();
            for (int i = 0; i < old.length(); i++) {
                JSONObject o = old.getJSONObject(i);
                if (!(o.optString("type").equals(type) && o.optString("name").equals(name))) {
                    neu.put(o);
                }
            }
            save(ctx, neu);
        } catch (JSONException ignored) {}
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private static void save(Context ctx, JSONArray arr) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_ARRAY, arr.toString()).apply();
    }
}
