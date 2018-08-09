package com.example.konshensx.firstapp;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        long timer_start = System.currentTimeMillis();
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
            responseJSON = response.toString();
        }
        this.jsonResponse = responseJSON;
        long timer_stop = System.currentTimeMillis();

        Log.i(TAG, "fetchJson: Time elapsed to get for the request in ms: " + (timer_stop - timer_start));
    }

    @Override
    protected String doInBackground(String... strings) {
        // TODO: might need to move this to the fetchJson function
        // currently it takes 130ms to execute this, which is kind of fast enough
        long startTime = System.currentTimeMillis();
        fetchJson(strings[0]);
        long stopTime = System.currentTimeMillis();
        long timeElapsed = stopTime - startTime;
        Log.i(TAG, "FetcherProfiler: Time taken to execute the fetchJson method in ms: " + timeElapsed);
        return this.jsonResponse;
    }

    protected void onPostExecute(String result) {
        listener.onTaskCompleted(this.jsonResponse, this.statusCode);
    }
}
