package dev.neyerdav.hackernewsreader;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputSteam = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputSteam);

            int data = inputStreamReader.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = inputStreamReader.read();
            }
            Log.i("URL Content", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        return null;
    }
}
