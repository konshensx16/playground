package com.example.konshensx.firstapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    SwitchCompat darkModeSwitch;

    public SettingFragment() {}

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // might need to change this ?
        return inflater.inflate(R.layout.setting_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        darkModeSwitch = getActivity().findViewById(R.id.dark_mode_switch);
        darkModeSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Toast.makeText(getContext(), "isChecked: " + isChecked , Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
