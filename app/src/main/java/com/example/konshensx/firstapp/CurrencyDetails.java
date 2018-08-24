package com.example.konshensx.firstapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class CurrencyDetails extends Fragment implements OnTaskCompleted {

    private String jsonResponse;
    private String displayCurrency;

    TextView nameDetailsHolderView;
    TextView nameHolder;
    TextView rankHolder;
    TextView priceHolder;
    TextView marketCapView;
    TextView volumeView;
    TextView totalSupplyView;
    TextView circulatingSupplyView;
    TextView change1hView;
    TextView change24hView;
    TextView change7dView;

    public CurrencyDetails() {}

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_currency_details, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get the view elements
        // Maybe i need to put these in the "onActivityCreated" method, in case of NullPtrException
        // TODO: some of these are returning null, REQUIRES SERIOUS ATTENTION
        nameDetailsHolderView = getActivity().findViewById(R.id.nameDetails);

        // initialize the views
        nameHolder = getActivity().findViewById(R.id.name);
        rankHolder = getActivity().findViewById(R.id.rank);
        priceHolder = getActivity().findViewById(R.id.price);
        marketCapView = getActivity().findViewById(R.id.marketcap);
        volumeView = getActivity().findViewById(R.id.volume);
        totalSupplyView = getActivity().findViewById(R.id.totalSupply);
        circulatingSupplyView = getActivity().findViewById(R.id.circulatingSupply);
        change1hView = getActivity().findViewById(R.id.change1h);
        change24hView = getActivity().findViewById(R.id.change24h);
        change7dView = getActivity().findViewById(R.id.change7d);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: get the display currency from the sharedPrefernces
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        displayCurrency = sharedPreferences.getString("DISPLAY_CURRENCY", "USD");
        // TODO : remove this line (useless)
//        setContentView(R.layout.activity_currency_details);

        // TODO: get rid of this because the id will be retrieved in the constructor

        /*
        // get the intent that started the activity and get the string

        Intent intent = getActivity().getIntent();
        // get the ID from the intent
        int message = intent.getIntExtra(Adapter.EXTRA_MESSAGE, 0);
        */
        // https://api.coinmarketcap.com/v2/ticker/1/
        String ajaxUrl = "https://api.coinmarketcap.com/v2/ticker/" + getArguments().getInt("currency_id") + "/?convert=" + displayCurrency;
        new Fetcher(this).execute(ajaxUrl);

    }

    @Override
    public void onTaskCompleted(String result, int statusCode) {
        // TODO: maybe make a class that transforms jsonString to object and stuff
        // need to handle when the result is null
        if (result != null)
        {
            this.jsonResponse = result;
        }

        try {
            JSONObject jsonObject = new JSONObject(this.jsonResponse);
            JSONObject currencyObject = jsonObject.getJSONObject("data");

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
            JSONObject usdData = quotesData.getJSONObject(displayCurrency);

            double price = usdData.getDouble("price");
            BigDecimal volume24 = new BigDecimal(usdData.getDouble("volume_24h"));
            BigDecimal marketCap = new BigDecimal(usdData.getDouble("market_cap"));
            double percentChange1H = usdData.getDouble("percent_change_1h");
            double percentChange24H = usdData.getDouble("percent_change_24h");
            double percentChange7D = usdData.getDouble("percent_change_7d");

            // instantiating the objects
            Quotes quotes = new Quotes(price, volume24, marketCap, percentChange1H, percentChange24H, percentChange7D);
            Currency currency = new Currency(id, name, symbol, websiteSlug, rank, circulatingSupply, totalSupply, maxSupply, quotes);

            // apply the data to the view elements
            nameDetailsHolderView.setText(String.format("%s DETAILS", name.toUpperCase()));
            nameHolder.setText(name);
            // in here i have to format the string because passing the rank which is an int will crash the app
            // TODO: maybe put this in a string resource instead of just this, for reusability
            rankHolder.setText(String.format("%d", rank));
            priceHolder.setText(String.format("%,.0f %s", price, displayCurrency));
            marketCapView.setText(String.format("%,.0f %s", marketCap, displayCurrency));
            volumeView.setText(String.format("%,.0f %s", volume24, displayCurrency));
            totalSupplyView.setText(String.format("%,.0f %s", totalSupply, displayCurrency));
            circulatingSupplyView.setText(String.format("%,.0f %s", circulatingSupply, symbol));

            getActivity().setTitle(String.format("%s Details", name));

            change1hView.setText(String.format("%,.2f %%", percentChange1H));
            if (percentChange1H > 0)
            {
                change1hView.setTextColor(Color.parseColor("#3AD084"));
            } else {
                change1hView.setTextColor(Color.parseColor("#F8748A"));
            }

            change24hView.setText(String.format("%,.2f %%", percentChange24H));
            if (percentChange24H > 0)   
            {
                change24hView.setTextColor(Color.parseColor("#3AD084"));
            } else {
                change24hView.setTextColor(Color.parseColor("#F8748A"));
            }
            change7dView.setText(String.format("%,.2f %%", percentChange7D));
            if (percentChange7D > 0)
            {
                change7dView.setTextColor(Color.parseColor("#3AD084"));
            } else {
                change7dView.setTextColor(Color.parseColor("#F8748A"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static CurrencyDetails newInstance(int currency_id) {
        CurrencyDetails myFragment = new CurrencyDetails();

        Bundle args = new Bundle();
        args.putInt("currency_id", currency_id);
        myFragment.setArguments(args);

        return myFragment;
    }
}
