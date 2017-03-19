package com.vin.moviedb.asynctask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.vin.moviedb.BuildConfig;
import com.vin.moviedb.R;
import com.vin.moviedb.adapter.MovieReviewAdapter;
import com.vin.moviedb.prensenter.MovieReview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vin on 14/3/17.
 */

public class FetchMovieReviewTask extends AsyncTask<Long, Void, ArrayList<MovieReview>> {
    private final String REVIEWS = "reviews";
    private final String TAG_MOVIE_REVIEW_TASK = FetchMovieReviewTask.class.getName();
    Context mContext;
    RecyclerView mListView;
    private MovieReviewAdapter mMovieReviewAdapter;

    public FetchMovieReviewTask(Context context, RecyclerView listView) {
        mContext = context;
        mListView = listView;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieReview> movieReview) {
        super.onPostExecute(movieReview);

        LinearLayout ll = (LinearLayout) mListView.getParent();

        if (movieReview != null && movieReview.size() != 0) {
            ll.findViewById(R.id.detail_movie_review_header).setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);

            mMovieReviewAdapter = new MovieReviewAdapter(movieReview);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            mListView.setLayoutManager(layoutManager);
            mListView.setHasFixedSize(true);
            mListView.setAdapter(mMovieReviewAdapter);
        } else {
            ll.findViewById(R.id.detail_movie_review_header).setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

    }

    @Override
    protected ArrayList<MovieReview> doInBackground(Long... params) {
        Log.d(TAG_MOVIE_REVIEW_TASK, "Starting Review sync");
        long mMovieId = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieReviewJsonStr = null;

        if (mMovieId != -1) {
            try {
                // movie db API path
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";

                final String APP_ID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendPath(Long.toString(mMovieId))
                        .appendPath(REVIEWS)
                        .appendQueryParameter(APP_ID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

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
                    return null;
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
                    return null;
                }
                movieReviewJsonStr = buffer.toString();
                return getMovieReviewsFromJson(movieReviewJsonStr);
            } catch (IOException e) {
                Log.e(TAG_MOVIE_REVIEW_TASK, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(TAG_MOVIE_REVIEW_TASK, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG_MOVIE_REVIEW_TASK, "Error closing stream", e);
                    }
                }
            }
        } else {
            Log.w(TAG_MOVIE_REVIEW_TASK, "Mobile device not connected to Internet.");
        }

        return null;
    }

    private ArrayList<MovieReview> getMovieReviewsFromJson(String reviewJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        // Movie information
        final String ID = "author";
        final String NAME = "content";

        // movie "list" array.
        final String REVIEW_LIST = "results";


        try {

            JSONObject movieJson = new JSONObject(reviewJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(REVIEW_LIST);

            ArrayList<MovieReview> movieReviewArrayList = new ArrayList<MovieReview>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {


                // These are the values that will be collected.
                String author;
                String content;

                // Get the JSON object representing a movie
                JSONObject reviews = movieArray.getJSONObject(i);

                author = reviews.getString(ID);
                content = reviews.getString(NAME);

                MovieReview movieReview = new MovieReview(author, content);
                movieReviewArrayList.add(movieReview);


            }

            return movieReviewArrayList;

        } catch (JSONException e) {
            Log.e(TAG_MOVIE_REVIEW_TASK, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}

