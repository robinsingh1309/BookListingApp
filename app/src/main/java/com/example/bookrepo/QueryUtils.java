package com.example.bookrepo;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String QUERY_UTILS_LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    // creating our own method to fetch the required details and connecting it to the server
    public static List<Book> fetchBooksDetails(String mURL) {

        URL url = createUrl(mURL);
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e(QUERY_UTILS_LOG_TAG, "Problem in making the Http request: ",e );
        }
        return extractBooksDetails(jsonResponse);
    }

    // this will instantiate the url
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(QUERY_UTILS_LOG_TAG, "Problem in building URL ", e);
        }
        return url;
    }

    // this method helps in connection with the server and after establishing connection
    // it will request the server for retrieval of information in the form of json format
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            // this will connect to the server
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(QUERY_UTILS_LOG_TAG, "Problem in response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(QUERY_UTILS_LOG_TAG, "Error in passing jsonResponse: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // it will convert input stream bytes into character stream tokens
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                // it will read next line
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // this method will extract out the information we need from JSON format file
    // it will return list of{@link Book} objects that has been built up from parsing a JSON response
    private static List<Book> extractBooksDetails(String jsonResponse) {

        // it will return {@param jsonResponse} null if no information is added in jsonResponse
        // which is coming from {@link readFromStream}
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        try {
            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray items = root.optJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject currentObject = items.getJSONObject(i);
                JSONObject volumeInfo = currentObject.getJSONObject("volumeInfo");
                // this will fetch the book title
                String title = volumeInfo.getString("title");
                // this will fetch the book author and checks for the condition if Author name
                // is present or not
                String author;
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if (!volumeInfo.isNull("authors")) {
                        author = (String) authors.get(0);
                    } else {
                        // if no author name then add name as Unknown Author
                        author = "*** Unknown Author***";
                    }
                } else {
                    // if there is no author Array in json response then
                    // display as Missing Author
                    author = "*** Missing Author *** ";
                }
                // this will fetch the book description
                String description = volumeInfo.getString("description");
                // this will fetch the book Image url
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("smallThumbnail");
                // passing all the required parameters to Book class
                // and adding them for collection of books and returning list of books
                books.add(new Book(title, author, description, imageUrl));
            }
        } catch (JSONException e) {
            Log.e(QUERY_UTILS_LOG_TAG, "Problem parsing the book JSON result", e);
        }
        return books;
    }
}
