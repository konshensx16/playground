package com.example.konshensx.firstapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
    // XXX moved these instance properties from the activity to here
    // testing the onpostexecute function to handle updating the UI with the necessary data
    private Context context;

    private OnTaskCompleted listener;
    private String jsonResponse;

    public Fetcher() { }

    public Fetcher(Context listener) {
        this.listener = (OnTaskCompleted) listener;
    }

    public void fetchJson(String link) {
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
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            //Here is your json in string format
            responseJSON = response.toString();
            // XXX shoudl the fetch function return the jsonString or the string parsed
//            try {
//                JSONObject jsonObject = new JSONObject(responseJSON);
//                JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                }
//            } catch (JSONException e)
//            {
//                e.printStackTrace();
//            }

        }
        this.jsonResponse = responseJSON;
    }

    @Override
    protected String doInBackground(String... strings) {
        // "https://api.coinmarketcap.com/v2/listings/"
        fetchJson(strings[0]);
        return this.jsonResponse;
    }

    protected void onPostExecute(String result) {
        listener.onTaskCompleted(this.jsonResponse);
    }
}
