package com.vin.moviedb.prensenter;

/**
 * Created by vin on 14/3/17.
 */

public class MovieReview {

    private String author;
    private String content;

    public MovieReview(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }


    public String getContent() {
        return content;
    }

}
