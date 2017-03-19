package com.vin.moviedb.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.vin.moviedb.data.MovieContract.PopularEntry;
import com.vin.moviedb.data.MovieContract.TopRatedEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by vin on 4/3/17.
 */

public class TestMovieContentProvider extends AndroidTestCase {

    private static long TEST_MOVIE_ID = 1234L;
    private static double PREVIOUS_POPULARITY = 123.456;
    private static double CURRENT_POPULARITY = 124.456;
    private static double PREVIOUS_USER_RATING = 8.5;
    private static double CURRENT_USER_RATING = 9.1;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createPopularMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PopularEntry.COLUMN_POPULAR_MOVIE_ID, TEST_MOVIE_ID);
        testValues.put(PopularEntry.COLUMN_POPULAR_TITLE, "90 Shades of Horror");
        testValues.put(PopularEntry.COLUMN_POPULAR_ORIGINAL_TITLE, "How in the Hell");
        testValues.put(PopularEntry.COLUMN_POPULAR_RELEASE_DATE, "09-09-2100");
        testValues.put(PopularEntry.COLUMN_POPULAR_USER_RATING, 8.5);
        testValues.put(PopularEntry.COLUMN_POPULAR_POPULARITY, PREVIOUS_POPULARITY);
        testValues.put(PopularEntry.COLUMN_POPULAR_POSTER_PATH, "/poster_path");
        testValues.put(PopularEntry.COLUMN_POPULAR_BACKDROP_POSTER_PATH, "/poster_path2");
        testValues.put(PopularEntry.COLUMN_POPULAR_OVERVIEW, "ohh no.. movie");

        return testValues;
    }

    static ContentValues createTopRatedMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID, TEST_MOVIE_ID);
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_TITLE, "Godfather");
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_ORIGINAL_TITLE, "GodFather - II");
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_RELEASE_DATE, "09-09-1987");
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_USER_RATING, PREVIOUS_USER_RATING);
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_POPULARITY, 234.55);
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_POSTER_PATH, "/poster_path");
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_BACKDROP_POSTER_PATH, "/poster_path2");
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_OVERVIEW, "Awesome movie");

        return testValues;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PopularEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                TopRatedEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PopularEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Popular table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TopRatedEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Top Rated table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieContract registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.vin.moviedb/popular
        String type = mContext.getContentResolver().getType(PopularEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.vin.moviedb/popular
        assertEquals("Error: the PopularEntry CONTENT_URI should return PopularEntry.CONTENT_TYPE",
                PopularEntry.CONTENT_TYPE, type);

        Long testMovieId = 94074L;
        // content://com.vin.moviedb/popular/94074
        type = mContext.getContentResolver().getType(
                PopularEntry.buildUriUsingMovieId(testMovieId));
        // vnd.android.cursor.item/com.vin.moviedb/popular
        assertEquals("Error: the PopularEntry CONTENT_URI  should return PopularEntry.CONTENT_TYPE",
                PopularEntry.CONTENT_ITEM_TYPE, type);

        // content://com.vin.moviedb/top_rated
        type = mContext.getContentResolver().getType(TopRatedEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.vin.moviedb/top_rated
        assertEquals("Error: the TopRatedEntry CONTENT_URI should return TopRatedEntry.CONTENT_TYPE",
                TopRatedEntry.CONTENT_TYPE, type);
    }

    public void testBasicPopularQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createPopularMovieValues();

        long popularRowId = db.insert(PopularEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert PopularEntry into the Database", popularRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor popularityCursor = mContext.getContentResolver().query(
                PopularEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        validateCursor("testBasicPopularQuery", popularityCursor, testValues);
    }

    public void testUpdatePopularTableUpdate() {
        final String TAG = "testUpdatePopularTable";
        Cursor cursor;
        double popValue;
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createPopularMovieValues();
        // insert via content provider
        Uri popularRowId = mContext.getContentResolver().insert(PopularEntry.CONTENT_URI, testValues);

        // read and check the inserted value for Popularity
        cursor = db.query(PopularEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Empty cursor returned. ", cursor.moveToFirst());
        popValue = cursor.getDouble(cursor.getColumnIndex(PopularEntry.COLUMN_POPULAR_POPULARITY));
        assertEquals("The popularity value is not as expected ", PREVIOUS_POPULARITY,
                popValue);
        Log.d(TAG, Double.toString(popValue));
        cursor.close();

        // change popularity to CURRENT_POPULARITY
        testValues.put(PopularEntry.COLUMN_POPULAR_POPULARITY, CURRENT_POPULARITY);
        // get the movie id and create Uri from the previously entered row
        Uri uri = PopularEntry.buildUriUsingMovieId(testValues.getAsLong(PopularEntry.COLUMN_POPULAR_MOVIE_ID));
        // Update query
        int updatedRows = mContext.getContentResolver().update(uri, testValues, null, null);
        assertEquals("Only 1 row should be updated", 1, updatedRows);

        // read and check the updated value for Popularity
        cursor = db.query(PopularEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Empty cursor returned. ", cursor.moveToFirst());
        popValue = cursor.getDouble(cursor.getColumnIndex(PopularEntry.COLUMN_POPULAR_POPULARITY));
        assertEquals("The popularity value is not as expected ", CURRENT_POPULARITY, popValue);
        Log.d(TAG, Double.toString(popValue));
        cursor.close();
        db.close();

    }

    public void testBasicTopRatedQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createTopRatedMovieValues();

        long topRatedRowId = db.insert(TopRatedEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert into the Database", topRatedRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor topCursor = mContext.getContentResolver().query(
                TopRatedEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        validateCursor("testBasicTopRatedQuery", topCursor, testValues);
    }

    public void testUpdateTopRatedTableUpdate() {
        final String TAG = "testUpdateTopRatedTable";
        Cursor cursor;
        double ratingValue;
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createTopRatedMovieValues();
        // insert via content provider
        mContext.getContentResolver().insert(TopRatedEntry.CONTENT_URI, testValues);

        // read and check the inserted value for Popularity
        cursor = db.query(TopRatedEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Empty cursor returned. ", cursor.moveToFirst());
        ratingValue = cursor.getDouble(cursor.getColumnIndex(TopRatedEntry.COLUMN_TOP_RATED_USER_RATING));
        assertEquals("The popularity value is not as expected ", PREVIOUS_USER_RATING,
                ratingValue);
        Log.d(TAG, Double.toString(ratingValue));
        cursor.close();

        // change popularity to CURRENT_USER_RATING
        testValues.put(TopRatedEntry.COLUMN_TOP_RATED_USER_RATING, CURRENT_USER_RATING);
        // get the movie id and create Uri from the previously entered row
        Uri uri = TopRatedEntry.buildUriUsingMovieId(testValues.getAsLong(TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID));
        // Update query
        int updatedRows = mContext.getContentResolver().update(uri, testValues, null, null);
        assertEquals("Only 1 row should be updated", 1, updatedRows);

        // read and check the updated value for Popularity
        cursor = db.query(TopRatedEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Empty cursor returned. ", cursor.moveToFirst());
        ratingValue = cursor.getDouble(cursor.getColumnIndex(TopRatedEntry.COLUMN_TOP_RATED_USER_RATING));
        assertEquals("The popularity value is not as expected ", CURRENT_USER_RATING, ratingValue);
        Log.d(TAG, Double.toString(ratingValue));
        cursor.close();
        db.close();

    }


}

