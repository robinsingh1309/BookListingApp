package com.example.bookrepo;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;
    // URL to fetch the result for different books
    private String googleBookApi = "";
    // creating global Adapter for Book
    private BookAdapter mAdapter;
    // Initialising Text View
    private TextView emptyTextView;

    private SearchView searchViewField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialising Search Button
        Button searchButton = findViewById(R.id.searchButton);
        // Initialising the search field section
        searchViewField = findViewById(R.id.search_view_field);
        searchViewField.onActionViewExpanded();
        searchViewField.setIconified(true);

        /**
         * when search button is clicked ensure that loader get restart
         * and new loader instance get created, after that
         * get the book name result from link{@searchViewField} and concatenate it to the url with
         * desired book name, ensure that secure host is enabled
         */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                updateQuery(searchViewField.getQuery().toString());
                //restarting the loader after new query is passed to the @googleBookApi url
                restartLoader();
                /**
                 * ensuring the user that fetching the details is in progress
                 * and setting the empty text view with text as {"Working on result"}
                 */
                View indicator = findViewById(R.id.loading_indicator);
                indicator.setVisibility(View.VISIBLE);
                emptyTextView.setText(getString(R.string.working));
            }
        });

        emptyTextView = findViewById(R.id.empty_view);
        // Find a reference to the {@link ListView} in the layout
        ListView listView = findViewById(R.id.list);
        // this will automatically handle the viewing of empty message
        listView.setEmptyView(emptyTextView);

        // creating an adapter that takes an empty array list
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // setting the adapter to @Link {ListView}
        listView.setAdapter(mAdapter);

        // It checks for the connection if data is ON / checks if connection is in Airplane mode or not
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // If there is connection then we will call the loader to fetch the details
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LoaderManager lm = getLoaderManager();
            lm.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // this will display the text when there is no connection to the user
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyTextView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // updating the new url after getting book name from @searchViewField section
        updateQuery(searchViewField.getQuery().toString());
        // creating the loader instance
        return new BookLoader(this, googleBookApi);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        // the progress bar will vanish if details are successfully fetched
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "There is no data to display ."
        emptyTextView.setText(getString(R.string.no_data));
        // Clear the adapter of previous book data
        mAdapter.clear();
        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // books set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, this);
    }

    // method for updating query in the @googleBookApi for desired result
    private void updateQuery(String searchValue) {
        /** if user typed book name as the jungle book,then below condition will ensure that
         * the desired book name must be passed as in @googleBookApi url as
         * https://.........?q=the+jungle+book&............
         */
        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }
        // this will update the query in @googleBookApi url
        googleBookApi = "https://www.googleapis.com/books/v1/volumes?q=" + searchValue + "&filter=paid-ebooks&maxResults=15";
    }

}