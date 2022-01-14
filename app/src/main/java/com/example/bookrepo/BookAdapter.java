package com.example.bookrepo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    // created HOST_SEPARATOR
    private static final String REQUEST_SEPARATOR = "http://";

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
        }

        // getting the book places and its attributes defined in {@link Book} class
        Book bookModel = getItem(position);

        // Initialising the bookTitle text view
        TextView bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        // Initialising the bookAuthor text view
        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.book_author);
        // Initialising the bookDescription text view
        TextView bookDescription = (TextView) listItemView.findViewById(R.id.book_description);
        // Initialising the bookImage image view
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_image);
        // getting the image url from the @link{bookModel}
        String img_url = bookModel.getImageURl();

        /** this method will ensure that when we connect it to the server,
         * we do get secure host instead of local host from the server
         * We have used the String.split() method and after that we have appended the
         * @newImageUrl with rest of the @img_url i.e, contains whole url
         * instead of http://
         * now @newImageUrl will hold https://...............
         */
        String newImageUrl = "https://";
        if (img_url.contains(REQUEST_SEPARATOR)) {
            String[] parts = img_url.split(REQUEST_SEPARATOR);
            // this will contain https://.....as a url for the image
            newImageUrl += parts[1];
        } else {
            // this will contain url of type http://........
            newImageUrl = img_url;
        }

        // setting the required book title
        bookTitle.setText(bookModel.getTitle());
        // setting the required book author
        bookAuthor.setText(bookModel.getAuthor());
        // setting the required book description
        bookDescription.setText(bookModel.getDescription());
        // used to retrieve image from server
        Picasso.get()
                .load(newImageUrl)
                // this will ensure another image if there is no image fetched from url
                .error(R.drawable.baseline_library_books_black_48)
                // setting the image to required @bookImage image view
                .into(bookImage);

        // returning the view that has been populated by different list item
        return listItemView;
    }
}
