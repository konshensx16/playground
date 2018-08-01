package com.example.konshensx.firstapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;


public class BitcoinIndex extends AppCompatActivity {
//    public static final String EXTRA_MESSAGE = "com.example.konshensx.firstapp.MESSAGE";

    BottomNavigationView navigation;
    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.activity_bitcoin_index);
            setTitle("Vertex Tracker");
            // get the tools bar to set the name later based on the page im in
            toolbar = getSupportActionBar();
            //Remove notification bar
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
            layoutParams.setBehavior(new BottomNavigationBehavior());

            loadFragment(new HomeFragment());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.search:
//                    toolbar.setTitle("Search for currencies");
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.settings:
//                    toolbar.setTitle("Settings");
                    fragment = new SettingFragment();
                    loadFragment(fragment);
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
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
