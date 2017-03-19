package com.vin.moviedb.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.vin.moviedb.BuildConfig;
import com.vin.moviedb.R;
import com.vin.moviedb.Utility;
import com.vin.moviedb.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by vin on 12/3/17.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final String TAG_MOVIE_SYNC_ADAPTER = MovieSyncAdapter.class.getName();
    private final Context mContext;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;

    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG_MOVIE_SYNC_ADAPTER, "Starting sync");
        String sortOrder = Utility.getPreferredSortOrder(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviePosterJsonStr = null;

        if (Utility.isOnline(getContext()) && !sortOrder.equals(getContext().getResources().getString(R.string.pref_sort_favourite))) {
            try {
                // movie db API path
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";

                final String APP_ID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendPath(sortOrder)
                        .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                //Log.d(TAG_MOVIE_SYNC_ADAPTER, builtUri.toString());

                URL url = new URL(builtUri.toString());

                // Create the request to MovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                moviePosterJsonStr = buffer.toString();
                getMovieDataFromJson(moviePosterJsonStr);
            } catch (IOException e) {
                Log.e(TAG_MOVIE_SYNC_ADAPTER, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(TAG_MOVIE_SYNC_ADAPTER, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG_MOVIE_SYNC_ADAPTER, "Error closing stream", e);
                    }
                }
            }
        }else{
            Log.w(TAG_MOVIE_SYNC_ADAPTER, "Mobile device not connected to Internet.");
        }

        return;
    }

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        // Movie information
        final String POSTER_PATH = "poster_path";
        final String BACKDROP_PATH = "backdrop_path";
        final String MOVIE_OVERVIEW = "overview";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String POPULARITY = "popularity";

        // movie "list" array.
        final String MDB_LIST = "results";

        final boolean isPopular = Utility.getPreferredSortOrder(mContext)
                .equals(mContext.getResources().getString(R.string.pref_sort_popular)) ?
                true :
                false;


        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);


            // Insert the new movies information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());


            for (int i = 0; i < movieArray.length(); i++) {
                // These are the values that will be collected.

                String posterPath;
                String backdropPosterPath;
                String movieOverview;
                String releaseDate;
                String title;
                long movieId;
                String originalTitle;
                Double userRating;
                Double popularity;


                // Get the JSON object representing a movie
                JSONObject movieDescription = movieArray.getJSONObject(i);

                movieId = movieDescription.getInt(MOVIE_ID);
                posterPath = movieDescription.getString(POSTER_PATH);
                backdropPosterPath = movieDescription.getString(BACKDROP_PATH);
                movieOverview = movieDescription.getString(MOVIE_OVERVIEW);
                releaseDate = movieDescription.getString(RELEASE_DATE);
                title = movieDescription.getString(TITLE);
                originalTitle = movieDescription.getString(ORIGINAL_TITLE);
                userRating = movieDescription.getDouble(VOTE_AVERAGE);
                popularity = movieDescription.getDouble(POPULARITY);


                /*
                 * Code segment to update App database with information fetch from online movie database.
                 * */

                if (isPopular) {
                    cVVector.add(createPopularTableRowValue(
                            movieId,
                            title,
                            originalTitle,
                            releaseDate,
                            userRating,
                            popularity,
                            posterPath,
                            backdropPosterPath,
                            movieOverview
                    ));

                } else {
                    cVVector.add(createTopRatedTableRowValue(
                            movieId,
                            title,
                            originalTitle,
                            releaseDate,
                            userRating,
                            popularity,
                            posterPath,
                            backdropPosterPath,
                            movieOverview
                    ));
                }
            }

            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            // insert in bulk.
            if (cVVector.size() > 0) {
                getContext().getContentResolver()
                        .bulkInsert(
                                isPopular ? MovieContract.PopularEntry.CONTENT_URI : MovieContract.TopRatedEntry.CONTENT_URI,
                                cVVector.toArray(cvArray)
                        );
            }


        } catch (JSONException e) {
            Log.e(TAG_MOVIE_SYNC_ADAPTER, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private ContentValues createPopularTableRowValue(
            long movieId, String title, String originalTitle,
            String releaseDate, Double userRating, Double popularity,
            String posterPath, String backdropPosterPath, String movieOverview) {

        ContentValues popularContentValues = new ContentValues();
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_MOVIE_ID, movieId);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_TITLE, title);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_ORIGINAL_TITLE, originalTitle);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_RELEASE_DATE, releaseDate);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_USER_RATING, userRating);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_POPULARITY, popularity);
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_POSTER_PATH,
                Utility.trimPosterPath(posterPath));
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_BACKDROP_POSTER_PATH,
                Utility.trimPosterPath(backdropPosterPath));
        popularContentValues.put(MovieContract.PopularEntry.COLUMN_POPULAR_OVERVIEW, movieOverview);

        return popularContentValues;
    }

    private ContentValues createTopRatedTableRowValue(
            long movieId, String title, String originalTitle,
            String releaseDate, Double userRating, Double popularity,
            String posterPath, String backdropPosterPath, String movieOverview) {


        ContentValues topRatedContentValues = new ContentValues();
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_MOVIE_ID, movieId);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_TITLE, title);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_ORIGINAL_TITLE, originalTitle);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_RELEASE_DATE, releaseDate);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_USER_RATING, userRating);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_POPULARITY, popularity);
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_POSTER_PATH,
                Utility.trimPosterPath(posterPath));
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_BACKDROP_POSTER_PATH,
                Utility.trimPosterPath(backdropPosterPath));
        topRatedContentValues.put(MovieContract.TopRatedEntry.COLUMN_TOP_RATED_OVERVIEW, movieOverview);


        return topRatedContentValues;
    }


}
