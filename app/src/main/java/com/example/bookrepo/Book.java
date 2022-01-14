package com.example.bookrepo;

public class Book {
    private String title;
    private String author;
    private String imageURl;
    private String description;

    public Book(String title, String author, String mDescription, String mImageURl) {
        this.title = title;
        this.author = author;
        this.description = mDescription;
        this.imageURl = mImageURl;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageURl() {
        return imageURl;
    }
}
