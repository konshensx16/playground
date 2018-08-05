package com.example.konshensx.firstapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Fetcher extends AsyncTask<String, Void, String>{

    private static final String TAG = "Fetcher";
    private int statusCode;
    private OnTaskCompleted listener;
    private String jsonResponse;

    public Fetcher() { }

    public Fetcher(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public void fetchJson(String link) {
        Log.i(TAG, "fetchJson: Fetcher sent a request to the end point");
        String responseJSON;
        URL url;
        StringBuffer response = new StringBuffer();
        try {
//            url = new URL("https://api.coinmarketcap.com/v2/ticker/");
            url = new URL(link);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url");
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // handle the response
            this.statusCode = conn.getResponseCode();
            // status == 429 which means to many request to end point
            // NOTES: i'm only sending one request when the app loads
            //        after trying another API i got a 200 status code, meaning success
            //        which means this error is due to the rate limit of the server
            //        which also means this app is just a waste right now :))
            Log.e(TAG, "fetchJson: Status code from end point " + this.statusCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            //Here is your json in string format
            responseJSON = response.toString();
        }
        this.jsonResponse = responseJSON;
    }

    @Override
    protected String doInBackground(String... strings) {
        // "https://api.coinmarketcap.com/v2/listings/"
        fetchJson(strings[0]);
        return this.jsonResponse;
    }

    // TODO: change the signature to include the status code and send it to the HomeFragment and display it in a Toast
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(this.jsonResponse, this.statusCode);
    }
}
