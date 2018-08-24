package com.example.konshensx.firstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "SettingFragmentClass";
    boolean isNightMode = false;

    SwitchCompat darkModeSwitch;
    Spinner currenciesSpinner;
    CoordinatorLayout coordinator_container;
    Snackbar snackbar,
            savedChangedSnackbar;

    ArrayAdapter<CharSequence> charSequenceArrayAdapter;
    SharedPreferences sharedPreferences;


    public SettingFragment() {}

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // might need to change this ?
        Log.i(TAG, "onCreateView: Called");
        return inflater.inflate(R.layout.setting_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: Called");
        coordinator_container = getActivity().findViewById(R.id.coordinator_container);
        darkModeSwitch = getActivity().findViewById(R.id.dark_mode_switch);
        currenciesSpinner = getActivity().findViewById(R.id.currency_spinner);

        // set the sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // TODO: set the switch to the state of the boolean value "NIGHT_MODE"
        isNightMode = sharedPreferences.getBoolean("NIGHT_MODE", false);
        darkModeSwitch.setChecked(isNightMode);

        // TODO: fill in the spinner with string-array

        charSequenceArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.currency_values, android.R.layout.simple_spinner_item);
        charSequenceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currenciesSpinner.setAdapter(charSequenceArrayAdapter);


        // TODO: get the currenct saved display value
        String savedDisplayCurrency = sharedPreferences.getString("DISPLAY_CURRENCY", "USD");
        currenciesSpinner.setSelection(charSequenceArrayAdapter.getPosition(savedDisplayCurrency));

        // Set the listeners on the components
        currenciesSpinner.setOnItemSelectedListener(this);
        darkModeSwitch.setOnCheckedChangeListener(this);

        savedChangedSnackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_container), "Your changed has been saved!", Snackbar.LENGTH_SHORT);
        snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_container), "Changes will take effect after restart!", Snackbar.LENGTH_LONG);

        /*************************************************************************/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Called");

        getActivity().setTitle(getString(R.string.setting_text));

        if (!this.checkInternetConnection()) {
            // TODO: show a snackbar
            final Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_container), "No internet connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!SettingFragment.this.checkInternetConnection())
                    {
                        snackbar.show();
                    }
                }
            });
            snackbar.show();
        }
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        // TODO: display toast or logs based on state of the switch
        switch (compoundButton.getId()) {
            case R.id.dark_mode_switch:

                // TODO: just set the preferences in the sharedPreferences file and wait till app restarts
                if (isChecked)
                {
                    // dark mode
                    sharedPreferences.edit().putBoolean("NIGHT_MODE", true).apply();
                } else {
                    //light mode: i don't think i need to set this too, just the first one is enough
                    sharedPreferences.edit().putBoolean("NIGHT_MODE", false).apply();
                }
                snackbar.show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // TODO: save the selected value in the sharedPreferences
        // display the value selected by the user
        String selectedItem = charSequenceArrayAdapter.getItem(i).toString();
        sharedPreferences.edit().putString("DISPLAY_CURRENCY", selectedItem).apply();
        savedChangedSnackbar.show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(getContext(), "No item selected", Toast.LENGTH_SHORT).show();

    }
}
