package com.vin.moviedb.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by vin on 4/3/17.
 */

public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 14190336L;

    public void testUriBuilder(){

        Uri uriPopular = MovieContract.PopularEntry.buildUriUsingMovieId(TEST_MOVIE_ID);
        Uri uriTopRated = MovieContract.TopRatedEntry.buildUriUsingMovieId(TEST_MOVIE_ID);

        assertEquals("Error: The popular Movie id was matched incorrectly.",
               MovieContract.getMovieIdFromUri(uriPopular) , TEST_MOVIE_ID);

        assertEquals("Error: The top rated Movie id was matched incorrectly.",
                MovieContract.getMovieIdFromUri(uriTopRated) , TEST_MOVIE_ID);

        assertEquals("Error: top rated Uri doesn't match our expected result",
                uriTopRated.toString(),
                "content://com.vin.moviedb/top_rated/14190336");

        assertEquals("Error: popular Uri doesn't match our expected result",
                uriPopular.toString(),
                "content://com.vin.moviedb/popular/14190336");

    }
}
