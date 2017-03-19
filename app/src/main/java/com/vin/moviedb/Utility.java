package com.vin.moviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vin on 26/2/17.
 */

public class Utility {
    public static String UTILITY_TAG = Utility.class.getName();

    private static DateFormat dateFormatFromMovieDb = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);
    private static DateFormat dateFormatUi = new SimpleDateFormat(
            "MMMM yyyy", Locale.ENGLISH);

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));
    }

    public static String getPreferredSortOrderActionBarString(Context context) {
        if (getPreferredSortOrder(context).equals(context.getString(R.string.pref_sort_popular))) {
            return context.getString(R.string.pref_sort_label_most_popular);
        } else if (getPreferredSortOrder(context).equals(context.getString(R.string.pref_sort_top))){
            return context.getString(R.string.pref_sort_label_top_rated);
        } else{
            return context.getString(R.string.pref_sort_label_favourite);
        }
    }

    public static String trimPosterPath(String posterPath) {
        return posterPath.substring(1, posterPath.length());
    }

    public static String getUiDateString(String apiDateString) {
        String uiDateString = "";
        try {
            Date apiDate = dateFormatFromMovieDb.parse(apiDateString);
            uiDateString = dateFormatUi.format(apiDate);
        } catch (ParseException pe) {
            Log.w(UTILITY_TAG, "Error in parsing date string for UI");
            // return apiDateString as received on parse error.
            return apiDateString;
        }
        return uiDateString;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
