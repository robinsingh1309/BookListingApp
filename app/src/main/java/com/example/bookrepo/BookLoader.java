package com.example.bookrepo;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String BOOK_LOADER_LOG_TAG = BookLoader.class.getName();
    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param mUrl    to load data from
     */
    public BookLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        // if there is no url present the return null
        if (mUrl == null) {
            return null;
        }
        // initialising list to be empty
        List<Book> books = null;
        try {
            // Perform the network request, parse the response, and extract a list of books.
            books = QueryUtils.fetchBooksDetails(mUrl);
        } catch (Exception e) {
            Log.e(BOOK_LOADER_LOG_TAG, "Error in parsing List of Books Details ", e);
        }
        return books;
    }
}
