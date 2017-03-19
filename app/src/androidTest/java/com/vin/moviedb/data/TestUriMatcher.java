
package com.vin.moviedb.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 14190336L;


    private static final Uri TEST_POPULAR_MOVIE_DIR = MovieContract.PopularEntry.CONTENT_URI;
    private static final Uri TEST_TOP_RATED_MOVIE_DIR = MovieContract.TopRatedEntry.CONTENT_URI;
    private static final Uri TEST_POPULAR_MOVIE_ID = MovieContract.PopularEntry.buildUriUsingMovieId(TEST_MOVIE_ID);
    private static final Uri TEST_TOP_RATED_MOVIE_ID = MovieContract.TopRatedEntry.buildUriUsingMovieId(TEST_MOVIE_ID);


    public void testUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: The TEST_POPULAR_MOVIE_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_POPULAR_MOVIE_DIR), MovieContentProvider.POPULAR);
        assertEquals("Error: The TEST_TOP_RATED_MOVIE_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_TOP_RATED_MOVIE_DIR), MovieContentProvider.TOP_RATED);
        assertEquals("Error: The TEST_POPULAR_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_POPULAR_MOVIE_ID), MovieContentProvider.POPULAR_WITH_MOVIE_ID);
        assertEquals("Error: The TEST_TOP_RATED_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_TOP_RATED_MOVIE_ID), MovieContentProvider.TOP_RATED_WITH_MOVIE_ID);
    }
}
