package com.example.konshensx.firstapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class SearchFragment extends Fragment implements OnTaskCompleted{
    EditText currency_search_input;
    private List<Listing> listingList;
    private String jsonResponse;
    private static final String TAG = "SearchFragment";
    final OnTaskCompleted listener;

    public SearchFragment() {
        this.listener = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        currency_search_input = view.findViewById(R.id.currency_search_input);

        // TODO: handle the input of the user, on change display the input in a Toast
        currency_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Where the code needs to be
                // TODO: might need to check if the length != 0 (optimization)
                // XXX: make the request to the API and search for the currencies
                // URL: https://api.coinmarketcap.com/v2/listings/?sort=rank
                // all result are always sorted by rank
                // TODO: test how much time will it take to load all currencies from listings into a list
                new Fetcher(listener).execute("https://api.coinmarketcap.com/v2/listings/?sort=rank");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onTaskCompleted(String result, int statusCode) {
        // XXX: parse the jsonResponse (string) to objects and add them to the currencyList
        Log.i(TAG, "onTaskCompleted: Response code from the API: " + statusCode);
        if (result != null) {
            this.jsonResponse = result;
        }

        // Should i create a new class called listing for the "listings".
        try {
            // TODO: create custom exceptions and check if the values are not null
            JSONObject jsonObject = new JSONObject(this.jsonResponse);
            JSONObject dataObject = jsonObject.getJSONObject("data");

            // iterate over the data objects
            Iterator<String> keys = dataObject.keys();
            while (keys.hasNext()) {
                int id = 0;
                // check if values are actually defined
                if (dataObject.isNull("id")) {
                    id = dataObject.getInt("id");
                }

                String name = "No name";
                // check if values are actually defined
                if (dataObject.isNull("name")) {
                    name = dataObject.getString("name");
                }

                String symbol = "No symbol";
                // check if values are actually defined
                if (dataObject.isNull("symbol")) {
                    symbol = dataObject.getString("symbol");
                }

                String website_slug = "No website_slug";
                // check if values are actually defined
                if (dataObject.isNull("website_slug")) {
                    website_slug = dataObject.getString("website_slug");
                }

                Listing listing = new Listing(id, name, symbol, website_slug);

                this.listingList.add(listing);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }




    }
}
