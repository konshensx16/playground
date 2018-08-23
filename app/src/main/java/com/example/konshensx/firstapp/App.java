package com.example.konshensx.firstapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application{

    private boolean isNightModeEnabled = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // load the night mode state here
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.isNightModeEnabled = sharedPreferences.getBoolean("NIGHT_MODE", false);
    }

    public boolean isNightModeEnabled()
    {
        return this.isNightModeEnabled;
    }

    public void setIsNightModeEnabled(boolean isNightModeEnabled)
    {
        this.isNightModeEnabled = isNightModeEnabled;
    }

}
