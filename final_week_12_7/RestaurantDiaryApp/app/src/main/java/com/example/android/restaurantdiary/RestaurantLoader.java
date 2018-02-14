package com.example.android.restaurantdiary;

/**
 * Created by Davey on 11/10/2017.
 */

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.restaurantdiary.utils.QueryUtils;

import java.util.List;

/** Loads a list of business' by using an AsyncTask to perform the
 * network request to the given URL. */
public class RestaurantLoader extends AsyncTaskLoader<List<Restaurant>> {

    /** Tag for log messages */
    private static final String LOG_TAG = RestaurantLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /** Constructs a new {@link RestaurantLoader}.
     * @param context of the activity
     * @param url to load data from */
    public RestaurantLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /** This is on a background thread. */
    @Override
    public List<Restaurant> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of yelpRestaurant.
        List<Restaurant> yelpRestaurant = QueryUtils.fetchYelpData(mUrl);
        return yelpRestaurant;
    }
}
