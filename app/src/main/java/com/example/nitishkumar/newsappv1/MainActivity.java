package com.example.nitishkumar.newsappv1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>>{

    private String requestURL ;
    private static final int NEWS_LOADER_ID = 1;
    private NewsDataArrayAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsList = (ListView)findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsList.setEmptyView(mEmptyStateTextView);
        mAdapter = new NewsDataArrayAdapter(this, new ArrayList<NewsData>());
        newsList.setAdapter(mAdapter);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsData currentNewsData = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNewsData.getNewsWebURL());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
        {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
        else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<NewsData>> onCreateLoader(int id, Bundle args) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);
        requestURL = getString(R.string.url_part1)+getString(R.string.from_date)+formattedDate+getString(R.string.url_part2)+getString(R.string.api_key);
        return new NewsLoader(this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsData>> loader, List<NewsData> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news);
        mAdapter.clear();
        if (data != null && !data.isEmpty())
        {
            mAdapter.addAll(data);
        }
        else
        {
            connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            loadingIndicator.setVisibility(View.GONE);
            if (networkInfo != null && networkInfo.isConnected())
            {
                mEmptyStateTextView.setText(R.string.no_news);
            }
            else
            {
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsData>> loader) {
        mAdapter.clear();
    }
}