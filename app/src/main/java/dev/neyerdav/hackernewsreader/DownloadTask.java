package dev.neyerdav.hackernewsreader;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, String> {

    SQLiteDatabase articleDB;

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

            JSONArray jsonArray = new JSONArray(result);
            int numberOfItems = 20;

            if (jsonArray.length() < 20) {
                numberOfItems = jsonArray.length();
            }

            articleDB.execSQL("DELETE FROM articles");
            for (int i = 0; i < numberOfItems; i++) {
                String articleId = jsonArray.getString(i);
                url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty");
                urlConnection = (HttpURLConnection) url.openConnection();
                inputSteam = urlConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputSteam);

                data = inputStreamReader.read();

                String articleInfo = "";
                while (data != -1) {
                    char current = (char) data;
                    articleInfo += current;
                    data = inputStreamReader.read();
                }
                JSONObject jsonObject = new JSONObject(articleInfo);
                if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                    String articleTitle = jsonObject.getString("title");
                    String articleUrl = jsonObject.getString("url");

                    url = new URL(articleUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    inputSteam = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputSteam);
                    data = inputStreamReader.read();
                    String articleContent = "";
                    while (data != -1) {
                        char current = (char) data;
                        articleContent += current;
                        data = inputStreamReader.read();
                    }
                    String sql = "INSERT INTO articles (articleID, title, content) VALUES (?, ?, ?)";
                    SQLiteStatement statement = articleDB.compileStatement(sql);
                    statement.bindString(1, articleId);
                    statement.bindString(2, articleTitle);
                    statement.bindString(1, articleContent);
                    statement.execute();
                }
            }
            Log.i("URL Content", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
