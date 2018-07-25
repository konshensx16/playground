package com.example.konshensx.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
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

            //Remove notification bar
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_bicoin_index);

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
        recyclerView = findViewById(R.id.rv_list);
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
