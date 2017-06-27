package com.example.android.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // LOG String
    public static final String LOG_TAG = MainActivity.class.getName();

    // Sample JSON response for a Guardian query */
    private static final String QUERY_NEWS_URL = "http://content.guardianapis.com/search?q=debates&api-key=test";

    // NewsAdapter global variable
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kick off an {@link AsyncTask} to perform the network request
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute(QUERY_NEWS_URL);


        // Sets ListView and ArrayAdapter
        ListView newsListView = (ListView) findViewById(R.id.list);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);


        //Opens a webpage related to the specific news article
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });
    }

    // Inner class AsyncTask
    private class NewsAsyncTask extends AsyncTask<String, Void, List<News>> {

        // doInBackground method
        @Override
        protected List<News> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<News> result = QueryUtils.fetchNewsdata(urls[0]);
            return result;
        }

        //onPostExecute method
        @Override
        protected void onPostExecute(List<News> data) {
            if (data == null) {
                return;
            }
            mAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}


