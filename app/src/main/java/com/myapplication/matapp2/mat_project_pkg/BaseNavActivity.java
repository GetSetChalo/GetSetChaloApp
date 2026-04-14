package com.myapplication.matapp2.mat_project_pkg;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.myapplication.matapp2.R;

public abstract class BaseNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ensure the system bars match the dark aesthetics
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.mat_secondary));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.mat_secondary));
        
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
        windowInsetsController.setAppearanceLightNavigationBars(false);
    }

    @Override
    public void setContentView(int layoutResID) {
        View baseView = getLayoutInflater().inflate(R.layout.mat_base_activity_with_nav, null);
        FrameLayout contentContainer = baseView.findViewById(R.id.base_content_container);

        getLayoutInflater().inflate(layoutResID, contentContainer, true);
        super.setContentView(baseView);
    }

    @Override
    public void setContentView(View view) {
        View baseView = getLayoutInflater().inflate(R.layout.mat_base_activity_with_nav, null);
        FrameLayout contentContainer = baseView.findViewById(R.id.base_content_container);

        contentContainer.addView(view);
        super.setContentView(baseView);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        View baseView = getLayoutInflater().inflate(R.layout.mat_base_activity_with_nav, null);
        FrameLayout contentContainer = baseView.findViewById(R.id.base_content_container);

        contentContainer.addView(view, params);
        super.setContentView(baseView, params);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        NavigationBarActivity.setup(this, getActiveTab());
    }

    protected abstract String getActiveTab();
}
