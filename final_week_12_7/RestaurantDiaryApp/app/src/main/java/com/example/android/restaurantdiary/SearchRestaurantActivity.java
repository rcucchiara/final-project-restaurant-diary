package com.example.android.restaurantdiary;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.android.restaurantdiary.data.RestaurantContract.ProspectiveRestaurantEntry;
import com.example.android.restaurantdiary.data.RestaurantContract.VisitedRestaurantEntry;

import java.util.ArrayList;
import java.util.List;

public class SearchRestaurantActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Restaurant>> {

    private static final String LOG_TAG = SearchRestaurantActivity.class.getName();

    /**URL for yelp data from the Yelp API */
    public String YELP_REQUEST_URL = "";

    public String requestBody = "https://api.yelp.com/v3/businesses/search?&term=restaurant";
    public String requestLocation = "&location=";
    public String requestSort = "&sort=0";

    /** Constant value for the YelpBusiness loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders. */
    private static final int BUSINESS_LOADER_ID = 1;

    /** * Adapter for the list of YelpBusiness Articles  */
    private RestaurantAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Content URI for the existing restaurant (null if it's a new restaurant) */
    private Uri mCurrentRestaurantUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Find a reference to the {@link EditText} in the layout
        final EditText searchLocation = findViewById(R.id.user_search_location);
        YELP_REQUEST_URL += searchLocation.getText().toString();

        // Find a reference to the {@link ListView} in the layout
        final ListView itemListView = (ListView) findViewById(R.id.restaurant_in_search_list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        itemListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of restaurants as input
        mAdapter = new RestaurantAdapter(this, new ArrayList<Restaurant>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        Button submitButton = findViewById(R.id.button);

        //submit button onclick listener to reload restaurant loader
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Find a reference to the {@link EditText} in the layout
                //we still have to url encode to get ascii.
                EditText editableLocation = findViewById(R.id.user_search_location);
                String locationString = editableLocation.getText().toString();
                String inputLocation = requestLocation + locationString;

                // Find a reference to the {@link EditText} in the layout
                //we still have to url encode to get ascii values.
                EditText editableName = findViewById(R.id.user_search_name);
                String nameString = editableName.getText().toString();
                String inputName = "+" + nameString;

                if(!TextUtils.isEmpty(locationString)) {
                    if(!TextUtils.isEmpty(nameString)) {
                        YELP_REQUEST_URL = requestBody + inputName + inputLocation + requestSort;
                    }
                    else YELP_REQUEST_URL = requestBody + inputLocation + requestSort;
                }
                getLoaderManager().restartLoader(BUSINESS_LOADER_ID, null, SearchRestaurantActivity.this );
            }
        });

        // Setup the item click listener to bring up popupmenu
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {

                final String restaurantName =((TextView)view.findViewById(R.id.search_name)).getText().toString();
                final String phoneNumber =((TextView)view.findViewById(R.id.search_phone)).getText().toString();
                String category =((TextView)view.findViewById(R.id.search_type_of_food)).getText().toString();
                final String address =((TextView)view.findViewById(R.id.search_address)).getText().toString();
                final String rating =((TextView)view.findViewById(R.id.search_rating)).getText().toString();

                //Popup menu to present option between Prospective and Visited Restaurant
                PopupMenu popup = new PopupMenu(SearchRestaurantActivity.this, itemListView);
                popup.getMenuInflater().inflate(R.menu.restaurant_db_selector, popup.getMenu());

                //OnClick listener, with Switch case for Prospective and Visited Classes
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.one:
                                // Create new intent to go to {@link EditorActivity}
                                Intent intent = new Intent(SearchRestaurantActivity.this, VisitedRestaurantFormActivity.class);

                                // Form the content URI that represents the specific Restaurant that was clicked on,
                                // by appending the "id" (passed as input to this method) onto the
                                // {@link VisitedRestaurantEntry#CONTENT_URI}.
                                Uri currentRestaurantUri =
                                        ContentUris.withAppendedId(VisitedRestaurantEntry.CONTENT_URI, id);

                                // Set the URI on the data field of the intent
                                intent.setData(currentRestaurantUri);
                                intent.putExtra("name", restaurantName);
                                intent.putExtra("address", address);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("rating", rating);

                                intent.putExtra("NameOfCallingClass", SearchRestaurantActivity.this.toString());

                                // Launch the {@link EditorActivity} to display the data for the current Restaurant.
                                startActivity(intent);
                                return true;

                            case R.id.two:
                                // Create new intent to go to {@link EditorActivity}
                                intent = new Intent(SearchRestaurantActivity.this, ProspectiveRestaurantFormActivity.class);

                                // Form the content URI that represents the specific Restaurant that was clicked on,
                                // by appending the "id" (passed as input to this method) onto the
                                // {@link ProspectiveRestaurantEntry#CONTENT_URI}.
                                currentRestaurantUri =
                                        ContentUris.withAppendedId(ProspectiveRestaurantEntry.CONTENT_URI, id);

                                // Set the URI on the data field of the intent
                                intent.setData(currentRestaurantUri);
                                intent.putExtra("name", restaurantName);
                                intent.putExtra("address", address);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("rating", rating);

                                intent.putExtra("NameOfCallingClass", SearchRestaurantActivity.this.toString());

                                // Launch the {@link EditorActivity} to display the data for the current Restaurant.
                                startActivity(intent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();//showing popup menu
            }
        });

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
    }

    @Override
    public Loader<List<Restaurant>> onCreateLoader(int i, Bundle bundle) {
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

