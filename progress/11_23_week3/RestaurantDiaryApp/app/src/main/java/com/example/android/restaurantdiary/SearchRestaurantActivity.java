package com.example.android.restaurantdiary;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Restaurant>> {


    private static final String LOG_TAG = SearchRestaurantActivity.class.getName();

    /**
     * URL for yelp data from the Yelp API
     */
    //Was this but we need to midify it
    //private static final YELP_REQUEST_URL =
    //public String temp_yelp_api_string =
    public String YELP_REQUEST_URL = "https://api.yelp.com/v3/businesses/search?text=del&location=";
    /** Constant value for the YelpBusiness loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders. */
    private static final int BUSINESS_LOADER_ID = 1;

    /** * Adapter for the list of YelpBusiness Articles  */
    private RestaurantAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Find a reference to the {@link EditText} in the layout
        EditText searchLocation = (EditText) findViewById(R.id.user_search);
        YELP_REQUEST_URL += searchLocation.getText().toString();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.restaurant_in_search_list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new RestaurantAdapter(this, new ArrayList<Restaurant>());


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BUSINESS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(com.example.android.restaurantdiary.R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(com.example.android.restaurantdiary.R.string.no_internet_connection);
        }

        // Setup Button to See Resteraunt List
        Button DavesButton = (Button) findViewById(R.id.button);

        DavesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Find a reference to the {@link EditText} in the layout
                EditText searchLocation = (EditText) findViewById(R.id.user_search);
                YELP_REQUEST_URL += searchLocation.getText().toString();
                //String YELP_REQUEST_URL = temp_yelp_api_string;
            }
        });
    }

    @Override
    public Loader<List<Restaurant>> onCreateLoader(int i, Bundle bundle) {

        //Uri baseUri = Uri.parse(YELP_REQUEST_URL);
        //Uri.Builder uriBuilder = baseUri.buildUpon();

        //return new BusinessLoader(this, uriBuilder.toString());
        return new RestaurantLoader(this, YELP_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Restaurant>> loader, List<Restaurant> yelpBusinesses) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(com.example.android.restaurantdiary.R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No yelpBusinesses found."
        mEmptyStateTextView.setText(com.example.android.restaurantdiary.R.string.no_restaurants);

        // Clear the adapter of previous business data
        mAdapter.clear();

        // If there is a valid list of {@link YelpBusiness}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (yelpBusinesses != null && !yelpBusinesses.isEmpty()) {
            mAdapter.addAll(yelpBusinesses);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Restaurant>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
