package com.example.konshensx.firstapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CurrencyDetails extends AppCompatActivity implements OnTaskCompleted {

    private String jsonResponse;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_details);

        // get the intent that started the activity and get the string
        Intent intent = getIntent();
        // get the ID from the intent
        int message = intent.getIntExtra(Adapter.EXTRA_MESSAGE, 0);

        // https://api.coinmarketcap.com/v2/ticker/1/
        String ajaxUrl = "https://api.coinmarketcap.com/v2/ticker/" + message;
        new Fetcher(this).execute(ajaxUrl);
        // get the view elements
        nameDetailsHolderView = findViewById(R.id.nameDetails);

        // initialize the views
        nameHolder = findViewById(R.id.name);
        rankHolder = findViewById(R.id.rank);
        priceHolder = findViewById(R.id.price);
        marketCapView = findViewById(R.id.marketcap);
        volumeView = findViewById(R.id.volume);
        totalSupplyView = findViewById(R.id.totalSupply);
        circulatingSupplyView = findViewById(R.id.circulatingSupply);
        change1hView = findViewById(R.id.change1h);
        change24hView = findViewById(R.id.change24h);
        change7dView = findViewById(R.id.change7d);
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

            // apply the data to the view elements
            nameDetailsHolderView.setText(String.format("%s DETAILS", name.toUpperCase()));
            nameHolder.setText(name);
            // in here i have to format the string because passing the rank which is an int will crash the app
            // TODO: maybe put this in a string resource instead of just this, for reusability
            rankHolder.setText(String.format("%d", rank));
            priceHolder.setText(String.format("%,.0f USD", price));
            marketCapView.setText(String.format("%,.0f $", marketCap));
            volumeView.setText(String.format("%,.0f $", volume24));
            totalSupplyView.setText(String.format("%,.0f $", totalSupply));
            circulatingSupplyView.setText(String.format("%,.0f %s", circulatingSupply, symbol));

            setTitle(String.format("%s Details", name));

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
}
