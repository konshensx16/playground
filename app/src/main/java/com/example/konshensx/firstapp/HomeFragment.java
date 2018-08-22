package com.example.konshensx.firstapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class will have a recyclerView and a bunch of listeners for different behaviours
 */
public class HomeFragment extends Fragment implements OnTaskCompleted {
    private static final String TAG = "HomeFragment";
    private int numberOfcalls;
    private String jsonResponse;
    private List<Currency> list;
    RecyclerView recyclerView;
    Adapter adapter;
    LinearLayoutManager linearLayoutManager;
    BottomNavigationView navigation;
    private EndlessRecyclerViewScrollListener scrollListener;

    //    CONSTS
    // the number of currencies to get in each request
    final int LIMIT = 10;
    // the number from where the next data should start
    // starting from 10 because the first set of data is loaded when the onCreate function is executed
    private int START_INDEX = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_layout, container, false);
    }

    /**
     * According to a stackOverflow comment, which states that documentation says this is the best place to access view and stuff
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // trying to see if this is what's causing the slow starting of the fragment and the 429 status code (rate limit)
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.rv_list);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            linearLayoutManager = new LinearLayoutManager(getContext());
            this.list = new ArrayList<>();
            adapter = new Adapter(getContext(), this.list, getActivity().getSupportFragmentManager());

            // not connected to the internet, display snackbar to the user
            if (!this.checkInternetConnection()) {
                final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_container), "No internet connection", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!HomeFragment.this.checkInternetConnection())
                        {
                            // TODO; read comments below
                            // not sure if this is needed anymore, might delete later
                            // or maybe i should dismiss the snackbar to null it ??
                            snackbar.show();
                        } else {
                            new Fetcher(new HomeFragment()).execute("https://api.coinmarketcap.com/v2/ticker/?limit=10&sort=rank");
                        }
                    }
                });
                snackbar.show();
            } else {
                // TODO: this need to run when internet connection available
                new Fetcher(this).execute("https://api.coinmarketcap.com/v2/ticker/?limit=10&sort=rank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method might be useless now that the coed is all moved to the Fetcher class
     * @param result
     */
    @Override
    public void onTaskCompleted(String result, int statusCode) {
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
                double circulatingSupply = 0.0;
                if (currencyObject.isNull("circulating_supply")) {
                    circulatingSupply = currencyObject.getDouble("circulating_supply");
                }
                double totalSupply = 0.0;
                if (currencyObject.isNull("total_supply")) {
                    totalSupply = currencyObject.getDouble("total_supply");
                }
                // BUG: check if the max supply is not null, because if it is, it's going to cause some problems
                double maxSupply = 0;
                if (!currencyObject.isNull("max_supply")) {
                    maxSupply = currencyObject.getDouble("max_supply");
                }

                JSONObject quotesData = currencyObject.getJSONObject("quotes");
                JSONObject usdData = quotesData.getJSONObject("USD");

                double price = usdData.getDouble("price");
                BigDecimal volume24 = new BigDecimal(0.0);
                if (usdData.isNull("volume_24h")) {
                    volume24 = new BigDecimal(usdData.getDouble("volume_24h"));
                }
                BigDecimal marketCap = new BigDecimal(0.0);
                if (usdData.isNull("market_cap")) {
                    marketCap = new BigDecimal(usdData.getDouble("market_cap"));
                }
                double percentChange1H = 0.0;
                if (!usdData.isNull("percent_change_1h")) {
                    percentChange1H = usdData.getDouble("percent_change_1h");
                }
                double percentChange24H = 0.0;
                if (!usdData.isNull("percent_change_24h")) {
                    percentChange24H = usdData.getDouble("percent_change_24h");
                }
                double percentChange7D = 0.0;
                if (!usdData.isNull("percent_change_7d")) {
                    percentChange7D = usdData.getDouble("percent_change_7d");
                }

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
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: Triggered, page: " + page + ", totalItemsCount: " + totalItemsCount);
                // triggered only when new data needs to be appended to the list
                // append new fetched data to the bottom of the list
                List<Currency> newList = loadNextSetOfDataFromAPI();
                list.addAll(newList);
                // notify the adapter of the changes so that it updates the recyclerView
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
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
        Log.i(TAG, "loadNextSetOfDataFromAPI: was called: " + this.numberOfcalls++ + " times");
        // get the next set of data
        // the limit could be a const so it's better to defined at the top
        // TODO: check for the limit of the list, i can't just keep fetching data , what if the server ran out of data, will that crash the app since i'm gonna get an empty string?
        // NOTE: this is causing RECURSION because the Fetcher will call the onTaskComplete when the job is finished, which will
        // call loadNextSetOfDataFromAPI, which will call Fetcher Again, which will call loadNextSetOfDataFromAPI which will call ......
        // you get the idea, so i need another work around, to get the next set of data
        // TODO: this has to change to something more dynamic, doing this currently yo avoid RATE_LIMIT problem
        if (this.numberOfcalls <= 30) {
            Log.i(TAG, "loadNextSetOfDataFromAPI: Fetcher was called, start_index: " + START_INDEX);
            new Fetcher(this).execute("https://api.coinmarketcap.com/v2/ticker/?start="+ START_INDEX +"&limit=" + LIMIT + "&sort=rank");
        }
        // INC the value of start by the LIMIT
        this.START_INDEX += LIMIT;

        return list;
    }

    /**
     * Checks if device has access to internet
     * @return
     */
    public boolean checkInternetConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
