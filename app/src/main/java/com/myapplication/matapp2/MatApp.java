package com.myapplication.matapp2;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Custom Application class.
 *
 * Reason: The base theme uses Theme.Material3.DayNight which makes Android toggle
 * the UI configuration based on the system dark/light mode setting. On first launch
 * this triggers an extra configuration change that leaves activities in an undrawn
 * state until the app is re-foregrounded. Locking the delegate to MODE_NIGHT_YES
 * (since every screen in this app uses a dark design) prevents that issue.
 */
public class MatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Lock to dark mode globally so DayNight never triggers a mid-session
        // configuration change that breaks the first-launch rendering.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
