package com.example.konshensx.firstapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BitcoinIndex extends AppCompatActivity implements OnTaskCompleted {
    public static final String EXTRA_MESSAGE = "com.example.konshensx.firstapp.MESSAGE";
    private String jsonResponse;
    private List<Currency> list;
    RecyclerView recyclerView;
    Adapter adapter;
    LinearLayoutManager linearLayoutManager;
    BottomNavigationView navigation;
    ActionBar toolbar;


    // the number of currencies to get in each request
    final int LIMIT = 10;
    // the number from where the next data should start
    // starting from 10 because the first set of data is loaded when the onCreate function is executed
    private int START_INDEX = 10;
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setTitle("Vertex Tracker");
            //Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_bitcoin_index);
            // get the tools bar to set the name later based on the page im in
            toolbar = getSupportActionBar();
            //Remove notification bar
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // NEED TO LOAD THE FRAGMENT BEFORE CALLING THE RV_LIST, OTHER WISE THE RV_LIST IS NOT AVAILABLE YET
            loadFragment(new HomeFragment());
            // TODO: need to love this to the home fragment (maybe)
            recyclerView = findViewById(R.id.rv_list);
            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


            Window window = getWindow();
            linearLayoutManager = new LinearLayoutManager(this);
//            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            this.list = new ArrayList<>();
            new Fetcher(this).execute("https://api.coinmarketcap.com/v2/ticker/?limit=10&sort=rank");

            adapter = new Adapter(this, this.list);


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
                    toolbar.setTitle("Vertex Tracker");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.search:
                    toolbar.setTitle("Search for currencies");
                    fragment = new SearchFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.settings:
                    toolbar.setTitle("Settings");
                    fragment = new SettingFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * This method might be useless now that the coed is all moved to the Fetcher class
     * @param result
     */
    @Override
    public void onTaskCompleted(String result) {
        if (result != null)
        {
            this.jsonResponse = result;
        }
        // XXX do the rest of the operations here ?
        // Parse the json string here instead of the fetcher
        try {
            JSONObject jsonObject = new JSONObject(this.jsonResponse);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            // This was changed from getting the items from all the list to just getting the first 20
            Iterator<String> keys = dataObject.keys();
            while (keys.hasNext()) {
                // right now i have access to each item of the list
                String index = keys.next(); // this is called index because in my case the key is the id of the currency
                JSONObject currencyObject = dataObject.getJSONObject(index);
                int id = currencyObject.getInt("id");
                String name = currencyObject.getString("name");
                String symbol = currencyObject.getString("symbol");
                String websiteSlug = currencyObject.getString("website_slug");
                int rank = currencyObject.getInt("rank");
                double circulatingSupply = currencyObject.getDouble("circulating_supply");
                double totalSupply = currencyObject.getDouble("total_supply");
                // BUG: check if the max supply is not null, because if it is, it's going to cause some problems
                double maxSupply = 0;
                if (!currencyObject.isNull("max_supply")) {
                    maxSupply = currencyObject.getDouble("max_supply");
                }

                JSONObject quotesData = currencyObject.getJSONObject("quotes");
                JSONObject usdData = quotesData.getJSONObject("USD");

                double price = usdData.getDouble("price");
                BigDecimal volume24 = new BigDecimal(usdData.getDouble("volume_24h"));
                BigDecimal marketCap = new BigDecimal(usdData.getDouble("market_cap"));
                double percentChange1H = usdData.getDouble("percent_change_1h");
                double percentChange24H = usdData.getDouble("percent_change_24h");
                double percentChange7D = usdData.getDouble("percent_change_7d");

                // instantiating the objects
                Quotes quotes = new Quotes(price, volume24, marketCap, percentChange1H, percentChange24H, percentChange7D);
                Currency currency = new Currency(id, name, symbol, websiteSlug, rank, circulatingSupply, totalSupply, maxSupply, quotes);
                this.list.add(currency);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        // Add the items to the list here instead of the onCreate method
        // setting the adapter to the recyclerView created
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // triggered only when new data needs to be appended to the list
                // append new fetched data to the bottom of the list
                List<Currency> newList = loadNextSetOfDataFromAPI();
                list.addAll(newList);
                // notify the adapter of the changes so that it updates the recyclerView
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemChanged(list.size() - 1);
                    }
                });
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * returns the next set of data
     * @return
     */
    private List<Currency> loadNextSetOfDataFromAPI() {
        List<Currency> list = new ArrayList<>();
        // get the next set of data
        // the limit could be a const so it's better to defined at the top
        new Fetcher(this).execute("https://api.coinmarketcap.com/v2/ticker/?start="+ START_INDEX +"&limit=" + LIMIT + "&sort=rank");
        // INC the value of start by the LIMIT
        this.START_INDEX += LIMIT;
        // adding just dummy data for new, later this needs to send a request to the end point and deserialize the data
//        Quotes quotes = new Quotes(100, new BigDecimal(12), new BigDecimal(12), 12, 12, 12);
//        list.add(new Currency(33, "konshensx", "X", "slougi", 12, 12, 12, 12, quotes));

        return list;
    }
}
