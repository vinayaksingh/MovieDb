package com.vin.moviedb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vin.moviedb.data.MovieContract.PopularEntry;
import com.vin.moviedb.data.MovieContract.TopRatedEntry;
import com.vin.moviedb.data.MovieContract.FavouriteEntry;

/**
 * Created by vin on 4/3/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 5;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + PopularEntry.TABLE_NAME + " (" +
                PopularEntry._ID + " INTEGER PRIMARY KEY," +
                PopularEntry.COLUMN_POPULAR_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_TITLE + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_USER_RATING + " REAL NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_POPULARITY + " REAL NOT NULL,  " +
                PopularEntry.COLUMN_POPULAR_POSTER_PATH + " TEXT NOT NULL,  " +
                PopularEntry.COLUMN_POPULAR_BACKDROP_POSTER_PATH + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_POPULAR_OVERVIEW + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +
                TopRatedEntry._ID + " INTEGER PRIMARY KEY," +
                TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_TITLE + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_RELEASE_DATE + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_USER_RATING + " REAL NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_POPULARITY + " REAL NOT NULL,  " +
                TopRatedEntry.COLUMN_TOP_RATED_POSTER_PATH + " TEXT NOT NULL,  " +
                TopRatedEntry.COLUMN_TOP_RATED_BACKDROP_POSTER_PATH + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_TOP_RATED_OVERVIEW + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavouriteEntry.COLUMN_FAVOURITE_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_TITLE + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_USER_RATING + " REAL NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_POPULARITY + " REAL NOT NULL,  " +
                FavouriteEntry.COLUMN_FAVOURITE_POSTER_PATH + " TEXT NOT NULL,  " +
                FavouriteEntry.COLUMN_FAVOURITE_BACKDROP_POSTER_PATH + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_FAVOURITE_OVERVIEW + " TEXT NOT NULL " +
                " );";

        // create Popular and top rated movie tables in the database.
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
