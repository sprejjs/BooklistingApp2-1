package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BooklistingActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Booklisting>> {

    private static final String LOG_TAG = BooklistingActivity.class.getName();

    private static final String BOOKLISTING_JSON =
            "https://www.googleapis.com/books/v1/volumes?maxResults=30&orderBy=newest&q=android";

    /**
     * Constant value for the booklist loader ID.
     */
    private static final int BOOKLISTING_LOADER_ID = 1;

    /**
     * Adapter for the list of books
     */
    private BooklistingAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    private String mQuery;
    private ProgressBar loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklisting_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView booklistingListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booklistingListView.setEmptyView(mEmptyStateTextView);

        loadingIndicator = (ProgressBar)findViewById(R.id.loading_indicator);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BooklistingAdapter(this, new ArrayList<Booklisting>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        booklistingListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOKLISTING_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public Loader<List<Booklisting>> onCreateLoader(int i, Bundle bundle) {
        String requestUrl = "";

        //String mQuery = mQuery (EditText R.id.search_edit_text);

        if (mQuery != null && mQuery != "") {
            requestUrl = BOOKLISTING_JSON + mQuery;
        } else {
            String defaultQuery = "android";
            requestUrl = BOOKLISTING_JSON + defaultQuery;
        }
        // Create a new loader for the given URL
        return new BooklistingLoader(this, requestUrl);

    }

    @Override
    public void onLoadFinished(Loader<List<Booklisting>> loader, List<Booklisting> books) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // /Set empty state view text to display "No books found"
        mEmptyStateTextView.setText(R.string.no_books_found);

        //Clear the adapter of previous booklisting data
        mAdapter.clear();
        //If there is a valid list of {@link Booklistings}s, then add them to the adapter's data set.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Booklisting>> loader) {
        //Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
