package com.example.konshensx.firstapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


public class BitcoinIndex extends AppCompatActivity {
    private static final String TAG = "BitcoinIndexClass";

    BottomNavigationView navigation;
    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // is night mode enabled
        boolean isNightMode = sharedPreferences.getBoolean("NIGHT_MODE", false);
        if (isNightMode) {
            // set the theme to night mode
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        } else {
            setTheme(R.style.ActivityTheme_Primary_Base_Light);
        }

        super.onCreate(savedInstanceState);
        // TODO: get the display currency from sharedPreferences
        String displayCurrency = sharedPreferences.getString("DISPLAY_CURRENCY", "USD");

        Toast.makeText(this, "DISPLAY_CURRENCY:" + displayCurrency, Toast.LENGTH_LONG);

        try {
            // log the current state at launch
            // MODE_NIGHT_NO = 1;
            // MODE_NIGHT_YES = 2;
            // MODE_NIGHT_AUTO = 0;
            // MODE_NIGHT_FOLLOW_SYSTEM = -1;
            // MODE_NIGHT_UNSPECIFIED = -100;
            // sDefaultNightMode = -1;
            Log.i(TAG, "onCreate: dark mode " + AppCompatDelegate.getDefaultNightMode());
            // load dark or light mode here, before everything else
//            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//                // set the theme to dark
////                setTheme(R.style.ActivityTheme_Primary_Base_Dark);
//                AppCompatDelegate
//            }

            if (savedInstanceState != null) {
                // activity coming back after being destroyed
                // remove all fragments, super.onCreate(null) didn't work
                this.clearAllFragment();
            }

            //Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.activity_bitcoin_index);
            setTitle("Vertex Tracker");
            // get the tools bar to set the name later based on the page im in
            toolbar = getSupportActionBar();

            //Remove notification bar
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            navigation = findViewById(R.id.navigation);

//            navigation.setVisibility(View.INVISIBLE);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
            layoutParams.setBehavior(new BottomNavigationBehavior());


            // check if device is connected to the internet
            if (!this.checkInternetConnection())
            {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_container), "No internet connection", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!BitcoinIndex.this.checkInternetConnection())
                        {
                            snackbar.show();
                        } else {
                            loadFragment(new HomeFragment(), "home");
                        }
                    }
                });
                snackbar.show();
            } else {
                loadFragment(new HomeFragment(), "home");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * clears all fragment before re-creating the activity for theme changing
     */
    private void clearAllFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment homeFragment = fragmentManager.findFragmentByTag("home");
        if (homeFragment != null) {
            fragmentTransaction.remove(homeFragment);
        }

        Fragment searchFragment = fragmentManager.findFragmentByTag("search");
        if (searchFragment != null) {
            fragmentTransaction.remove(searchFragment);
        }

        Fragment settingFragment = fragmentManager.findFragmentByTag("settings");
        if (settingFragment != null) {
            fragmentTransaction.remove(settingFragment);
        }

        Fragment currencyDetailsFragment = fragmentManager.findFragmentByTag("currencyDetails");
        if (currencyDetailsFragment != null) {
            fragmentTransaction.remove(currencyDetailsFragment);
        }

        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    loadFragment(fragment, "home");
                    return true;
                case R.id.search:
//                    toolbar.setTitle("Search for currencies");
                    fragment = new SearchFragment();
                    loadFragment(fragment, "search");
                    return true;
                case R.id.settings:
//                    toolbar.setTitle("Settings");
                    fragment = new SettingFragment();
                    loadFragment(fragment, "settings");
                    return true;
            }
            return false;
        }
    };

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment, String tag) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in
                , android.R.anim.fade_out);
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Checks if device has access to internet
     * @return
     */
    public boolean checkInternetConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
