package com.vin.moviedb.prensenter;

/**
 * Created by vin on 14/3/17.
 */

public class MovieTrailer {

    private String id;
    private String name;
    private String youtubeKey;

    public MovieTrailer(String id, String name, String youtubeKey){
        this.id = id;
        this.name = name;
        this.youtubeKey = youtubeKey;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return this.id;
    }

    public String getYoutubeKey(){
        return this.youtubeKey;
    }
}
