package dev.neyerdav.hackernewsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    SQLiteDatabase articleDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articleDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleid INTEGER, title VARCHAR, content VARCHAR)");
        DownloadTask task = new DownloadTask();

        try{
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        } catch(Exception e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("content", content.get(position));
                startActivity(intent);
            }
        });
        updateListView();
    }

    public void updateListView() {
        Cursor c = articleDB.rawQuery("SELECT * FROM articles", null);
        int contentIndex = c.getColumnIndex("content");
        int titleIndex = c.getColumnIndex("title");

        if(c.moveToFirst()){
            titles.clear();
            content.clear();
            do {
                titles.add(c.getString(titleIndex));
                content.add(c.getString(contentIndex));
            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}