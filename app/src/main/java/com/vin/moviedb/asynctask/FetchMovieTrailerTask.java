package com.vin.moviedb.asynctask;

/**
 * Created by vin on 14/3/17.
 */

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.vin.moviedb.BuildConfig;
import com.vin.moviedb.DetailMovieFragment;
import com.vin.moviedb.MovieFragment;
import com.vin.moviedb.R;
import com.vin.moviedb.adapter.MovieTrailerAdapter;
import com.vin.moviedb.prensenter.MovieTrailer;

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
 *
 * */
public class FetchMovieTrailerTask extends AsyncTask<Long, Void, ArrayList<MovieTrailer>> {
    private final String VIDEOS = "videos";
    private final String YOUTUBE_PARAM = "v";
    private final String TAG_MOVIE_TRAILER_TASK = FetchMovieTrailerTask.class.getName();
    private Context mContext;
    private RecyclerView mListView;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    private DetailMovieFragment mDetailMovieFragment;


    public FetchMovieTrailerTask(DetailMovieFragment detailMovieFragment, Context context, RecyclerView listView) {
        mContext = context;
        mListView = listView;
        mDetailMovieFragment = detailMovieFragment;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieTrailer> movieTrailers) {
        super.onPostExecute(movieTrailers);

        LinearLayout ll = (LinearLayout) mListView.getParent();

        if(movieTrailers != null && movieTrailers.size() > 0) {
            ll.findViewById(R.id.detail_movie_trailer_header).setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);

            mMovieTrailerAdapter = new MovieTrailerAdapter(movieTrailers);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext ,LinearLayoutManager.HORIZONTAL, false);
            mListView.setLayoutManager(layoutManager);
            mListView.setHasFixedSize(true);
            mListView.setAdapter(mMovieTrailerAdapter);


            // build share intent
            Uri shareIntentUri = Uri.parse("http://www.youtube.com/watch")
                    .buildUpon().appendQueryParameter(YOUTUBE_PARAM, movieTrailers.get(0).getYoutubeKey())
                    .build();
            mDetailMovieFragment.setShareIntent(shareIntentUri);
        }else{
            ll.findViewById(R.id.detail_movie_trailer_header).setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);

        }
    }

    @Override
    protected ArrayList<MovieTrailer> doInBackground(Long... params) {
        Log.d(TAG_MOVIE_TRAILER_TASK, "Starting Trailer sync");
        long mMovieId = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieTrailerJsonStr = null;

        if (mMovieId != -1) {
            try {
                // movie db API path
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";

                final String APP_ID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendPath(Long.toString(mMovieId))
                        .appendPath(VIDEOS)
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
                movieTrailerJsonStr = buffer.toString();
                return getMovieTrailersFromJson(movieTrailerJsonStr);
            } catch (IOException e) {
                Log.e(TAG_MOVIE_TRAILER_TASK, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(TAG_MOVIE_TRAILER_TASK, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG_MOVIE_TRAILER_TASK, "Error closing stream", e);
                    }
                }
            }
        } else {
            Log.w(TAG_MOVIE_TRAILER_TASK, "Mobile device not connected to Internet.");
        }

        return null;
    }

    private ArrayList<MovieTrailer> getMovieTrailersFromJson(String trailerJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        // Movie information
        final String ID = "id";
        final String NAME = "name";
        final String YOUTUBE_KEY = "key";
        final String TYPE = "type";

        final String TRAILER = "Trailer";

        // movie "list" array.
        final String VIDEO_LIST = "results";


        try {

            JSONObject movieJson = new JSONObject(trailerJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(VIDEO_LIST);

            ArrayList<MovieTrailer> movieTrailerArrayList = new ArrayList<MovieTrailer>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {


                // These are the values that will be collected.
                String id;
                String name;
                String youtubeKey;
                String type;

                // Get the JSON object representing a movie
                JSONObject trailer = movieArray.getJSONObject(i);

                type = trailer.getString(TYPE);
                if (type.equals(TRAILER)) {
                    id = trailer.getString(ID);
                    name = trailer.getString(NAME);
                    youtubeKey = trailer.getString(YOUTUBE_KEY);
                    MovieTrailer movieTrailer = new MovieTrailer(id, name, youtubeKey);
                    movieTrailerArrayList.add(movieTrailer);
                }

            }

            return movieTrailerArrayList;

        } catch (JSONException e) {
            Log.e(TAG_MOVIE_TRAILER_TASK, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}

