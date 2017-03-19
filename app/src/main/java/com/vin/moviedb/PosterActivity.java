package com.vin.moviedb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vin.moviedb.callback.GridClickCallback;
import com.vin.moviedb.sync.MovieSyncAdapter;

public class PosterActivity extends AppCompatActivity implements GridClickCallback {

    private static final String DETAIL_MOVIE_FRAGMENT_TAG = "DFAG";
    public boolean splitView;
    private String mGridOrderChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);
        mGridOrderChange = Utility.getPreferredSortOrder(this);
        MovieSyncAdapter.initializeSyncAdapter(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            splitView = true;

            // load fragment ont the right side
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailMovieFragment(), DETAIL_MOVIE_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            splitView = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.poster_activity_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String gridOrderChange = Utility.getPreferredSortOrder(this);
        if (mGridOrderChange != null && !mGridOrderChange.equals(gridOrderChange)) {
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_poster);
            if (mf != null) {
                mf.onGridOrderChanged();
            }
            mGridOrderChange = gridOrderChange;
        }
        getSupportActionBar().setTitle(Utility.getPreferredSortOrderActionBarString(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemSelected(Uri uri, boolean startDetailActivity) {

        if (splitView) {
            // replace the right fragment with content relevant to left side.
            loadDetailView(uri);

        } else {
            if (startDetailActivity) {
                // start detail activity.
                Intent intent = new Intent(this, DetailMovieActivity.class)
                        .setData(uri);
                startActivity(intent);
            }
        }
    }

    private void loadDetailView(Uri uri) {
        DetailMovieFragment fragment = new DetailMovieFragment();
        if (uri != null) {
            Bundle args = new Bundle();
            args.putParcelable(DetailMovieFragment.DETAIL_URI, uri);

            DetailMovieFragment df = (DetailMovieFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_MOVIE_FRAGMENT_TAG);
            if (df != null) {
                fragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAIL_MOVIE_FRAGMENT_TAG)
                        .commit();
            }
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_MOVIE_FRAGMENT_TAG)
                    .commit();
        }

    }
}
