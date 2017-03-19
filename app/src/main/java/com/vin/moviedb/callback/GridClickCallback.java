package com.vin.moviedb.callback;


import android.net.Uri;

/**
 * Created by vin on 5/3/17.
 */

public interface GridClickCallback {
    void onGridItemSelected(Uri uri, boolean startDetailActivity);

}
