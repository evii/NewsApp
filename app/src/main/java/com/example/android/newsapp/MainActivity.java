package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;


public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks <List<News>> {

    // LOG String
    public static final String LOG_TAG = MainActivity.class.getName();

    // Sample JSON response for a Guardian query */
    private static final String QUERY_NEWS_URL = "https://content.guardianapis.com/search?";

    // NewsAdapter global variable
    private NewsAdapter mAdapter;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets ListView and ArrayAdapter
        ListView newsListView = (ListView) findViewById(R.id.list);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);

        // creates EmptyView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Creates ConnectivityManager to check connection
        ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Tests, if there is connection
        if(networkInfo!= null && networkInfo.isConnected() ){
        // Creates LoaderManager and initialiazes it
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);}
        else {
            View loadingIndicator = findViewById(R.id.load_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_connection);
        }

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

    // Loaders onCreate - returns new NewsLoader
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        //Biulds QUERY URL from preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String topic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));
        String topicAdj = topic.replace(" ", " AND ");
        Uri baseUri = Uri.parse(QUERY_NEWS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", topicAdj);
        uriBuilder.appendQueryParameter("api-key", "test");
        return new NewsLoader(this, uriBuilder.toString());
    }

    // Loaders onLoadFinished - same as onPostExecute in Async
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        if (news == null) {
            return;
        }
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.load_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();
        if (news != null && !news.isEmpty()) {
        mAdapter.addAll(news);
        }
    }

    // reseting Loader and Adapter
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    // Updating the results of the search according to preferences after going back from preferences
    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    // Creating menu in the app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    // after clicking on the menu, new activity with settings opens
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    }



