package com.vin.moviedb.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vin on 27/2/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.vin.moviedb";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_FAVOURITE = "favourite";

    public static long getMovieIdFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(1));
    }

    public static final class PopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        // Table name
        public static final String TABLE_NAME = "popular";
        // Column names
        public static final String COLUMN_POPULAR_MOVIE_ID = "popular_movie_id";
        public static final String COLUMN_POPULAR_TITLE = "popular_title";
        public static final String COLUMN_POPULAR_ORIGINAL_TITLE = "popular_original_title";
        public static final String COLUMN_POPULAR_RELEASE_DATE = "popular_release_date";
        public static final String COLUMN_POPULAR_USER_RATING = "popular_user_rating";
        public static final String COLUMN_POPULAR_POPULARITY = "popular_popularity";
        public static final String COLUMN_POPULAR_POSTER_PATH = "popular_poster_path";
        public static final String COLUMN_POPULAR_BACKDROP_POSTER_PATH = "popular_backdrop_poster_path";
        public static final String COLUMN_POPULAR_OVERVIEW = "popular_overview";


        public static Uri buildUriUsingMovieId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .build();
        }

    }

    public static final class TopRatedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        // Table name
        public static final String TABLE_NAME = "top_rated";
        // Column names
        public static final String COLUMN_TOP_RATED_MOVIE_ID = "top_movie_id";
        public static final String COLUMN_TOP_RATED_TITLE = "top_title";
        public static final String COLUMN_TOP_RATED_ORIGINAL_TITLE = "top_original_title";
        public static final String COLUMN_TOP_RATED_RELEASE_DATE = "top_release_date";
        public static final String COLUMN_TOP_RATED_USER_RATING = "top_user_rating";
        public static final String COLUMN_TOP_RATED_POPULARITY = "top_popularity";
        public static final String COLUMN_TOP_RATED_POSTER_PATH = "top_poster_path";
        public static final String COLUMN_TOP_RATED_BACKDROP_POSTER_PATH = "top_rated_backdrop_poster_path";
        public static final String COLUMN_TOP_RATED_OVERVIEW = "top_overview";


        public static Uri buildUriUsingMovieId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .build();
        }


    }

    public static final class FavouriteEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;

        // Table name
        public static final String TABLE_NAME = "favourite";
        // Column names
        public static final String COLUMN_FAVOURITE_MOVIE_ID = "favourite_movie_id";
        //public static final String COLUMN_FAVOURITE_MOVIE_CATEGORY = "movie_category";
        public static final String COLUMN_FAVOURITE_TITLE = "favourite_title";
        public static final String COLUMN_FAVOURITE_ORIGINAL_TITLE = "favourite_original_title";
        public static final String COLUMN_FAVOURITE_RELEASE_DATE = "favourite_release_date";
        public static final String COLUMN_FAVOURITE_USER_RATING = "favourite_user_rating";
        public static final String COLUMN_FAVOURITE_POPULARITY = "favourite_popularity";
        public static final String COLUMN_FAVOURITE_POSTER_PATH = "favourite_poster_path";
        public static final String COLUMN_FAVOURITE_BACKDROP_POSTER_PATH = "favourite_backdrop_poster_path";
        public static final String COLUMN_FAVOURITE_OVERVIEW = "favourite_overview";

        public static Uri buildUriUsingMovieId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .build();
        }

//        public static Uri buildUriWithMovieIdAndCategory(long movieId, String category) {
//            return CONTENT_URI.buildUpon()
//                    .appendPath(Long.toString(movieId))
//                    .appendPath(category)
//                    .build();
//        }
//
//        public static String getMovieCategoryFromUri(Uri uri) {
//            return uri.getPathSegments().get(2);
//        }

    }
}
