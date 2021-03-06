package com.example.konshensx.firstapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements OnTaskCompleted{
    private List<Listing> listingList = new ArrayList<>();
    private final List<Listing> searchList = new ArrayList<>();
    private String jsonResponse;
    private SearchAdapter searchAdapter;
    final OnTaskCompleted listener;
    private static final String TAG = "SearchFragment";

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText currency_search_input;
    TextView search_title_holer;

    public SearchFragment() {
        this.listener = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get & set the recycler view
        recyclerView = getActivity().findViewById(R.id.search_recyclerView);
        recyclerView.setAdapter(new SearchAdapter(getActivity(), this.searchList, getActivity().getSupportFragmentManager()));
        // TODO: check if the recyclerView is already attached to a window
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currency_search_input = view.findViewById(R.id.currency_search_input);
        search_title_holer = view.findViewById(R.id.search_title_holder);

        getActivity().setTitle(getString(R.string.search_text));

        // TODO: check for internet connection
        if (!this.checkInternetConnection()) {
            // TODO: show a snackbar
            final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_container), "No internet connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!SearchFragment.this.checkInternetConnection())
                    {
                        snackbar.show();
                    } else {
                        new Fetcher(listener).execute("https://api.coinmarketcap.com/v2/listings/?sort=rank");
                    }
                }
            });
            snackbar.show();
        } else {

            // TODO: might need to check if the length != 0 (optimization)
            // XXX: make the request to the API and search for the currencies
            // URL: https://api.coinmarketcap.com/v2/listings/?sort=rank
            // all result are always sorted by rank
            new Fetcher(listener).execute("https://api.coinmarketcap.com/v2/listings/?sort=rank");
        }


        currency_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "onTextChanged: Text changed to: " + charSequence);
                // clear the list off the previous items
                searchList.clear();
                // TODO: search within the list for the user input 'currency in question'
                // probably just do a linear search for now and then improve it later, which will only get one result atm
                try {
                    for (int index = 0; index < listingList.size(); index++) {
                        Listing listingObject = listingList.get(index);
                        // checking if the name contains the typed character (user input)
                        // probably make the user input lower case too, for consistency and yielding better result
                        if (listingObject.getName().toLowerCase().contains(charSequence)
                                ||
                                listingObject.getSymbol().toLowerCase().contains(charSequence)) {
                            // add the item (the one found) to the list
                            searchList.add(listingObject);
                        }
                    }
                    // notify the adapter of the changes made to the list
                    searchAdapter.notifyDataSetChanged();
                    Log.i(TAG, "onTextChanged: Just to debug the searchList");
                    // Set the search_title_holder to what the user is searching for
                    if (charSequence.length() > 0)
                    {
                        String title_holder;
                        // TODO: if no results were found display a no results found message
                        if (searchList.size() == 0) {
                            // list is empty
                            title_holder = getString(R.string.search_title_no_results, charSequence);
                        } else {
                            title_holder = getString(R.string.search_title_holder, charSequence, searchList.size());

                        }
                        search_title_holer.setText(title_holder);
                    } else {
                        // clear the search list when the EditText is empty
                        searchList.clear();
                        search_title_holer.setText(getString(R.string.currency_recent_search));
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupTransition();
        try {
            linearLayoutManager = new LinearLayoutManager(getContext());

            searchAdapter = new SearchAdapter(getContext(), this.searchList, getActivity().getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(String result, int statusCode) {
        // XXX: parse the jsonResponse (string) to objects and add them to the currencyList
        try {
            // TODO: remove the logs
            Log.i(TAG, "onTaskCompleted: Response code from the API: " + statusCode);
            if (result != null) {
                this.jsonResponse = result;

            } else {
                throw new Exception("JsonResponse string is empty");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Should i create a new class called listing for the "listings" ?
        try {
            JSONObject jsonObject = new JSONObject(this.jsonResponse);
            // The listing is actually an array of JsonObjects and not an object of objects, using an array is better than an iterator, i guess
            JSONArray dataArray = jsonObject.getJSONArray("data");

            // iterate over the data objects
            for (int index = 0; index < dataArray.length(); index++)
            {
                JSONObject dataArrayObject = dataArray.getJSONObject(index);
                int id = 0;
                // check if values are actually defined
                if (!(dataArrayObject.isNull("id"))) {
                    id = dataArrayObject.getInt("id");
                }

                String name = "No name";
                // check if values are actually defined
                if (!(dataArrayObject.isNull("name"))) {
                    name = dataArrayObject.getString("name");
                }

                String symbol = "No symbol";
                // check if values are actually defined
                if (!(dataArrayObject.isNull("symbol"))) {
                    symbol = dataArrayObject.getString("symbol");
                }

                String website_slug = "No website_slug";
                // check if values are actually defined
                if (!(dataArrayObject.isNull("website_slug"))) {
                    website_slug = dataArrayObject.getString("website_slug");
                }

                Listing listing = new Listing(id, name, symbol, website_slug);

                this.listingList.add(listing);

                // TODO: maybe look for somewhere else to put this code
                recyclerView.setAdapter(searchAdapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                // Set any required events listeners (probably scroll, for the endless scroll effect)
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: SearchFragment was destroyed");
    }
}
