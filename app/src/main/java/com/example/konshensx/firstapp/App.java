package com.example.konshensx.firstapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application{

    public static App instance = null;

    private static boolean isNightModeEnabled = false;
    private static SharedPreferences sharedPreferences;
    private static String displayCurrency;

    public App()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.displayCurrency = getDisplayCurrency();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // load the night mode state here
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        this.isNightModeEnabled = sharedPreferences.getBoolean("NIGHT_MODE", false);

        // TODO: load the display currency here instead of onCreate of every fragment

    }

    public static App getInstance(){
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public static String getDisplayCurrency()
    {
        return sharedPreferences.getString("DISPLAY_CURRENCY", "USD");
    }

    public static boolean isNightModeEnabled()
    {
        return isNightModeEnabled;
    }

    public void setIsNightModeEnabled(boolean isNightModeEnabled)
    {
        this.isNightModeEnabled = isNightModeEnabled;
    }

}
