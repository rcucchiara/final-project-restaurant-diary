package com.example.android.restaurantdiary;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.restaurantdiary.data.RestaurantContract;

import java.util.ArrayList;

public class HasVisited extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Logger tag */
    public static final String LOG_TAG = HasVisited.class.getSimpleName();

    private RestaurantAdapter mAdapter;
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_has_visited);

        ListView RestaurantListView = findViewById(R.id.list_visited);

        mEmptyStateTextView = findViewById(R.id.empty_view_visited);
        RestaurantListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new RestaurantAdapter(this, new ArrayList<Restaurant>());

        RestaurantListView.setAdapter(mAdapter);



        /**
         *  Clicking this button should allow the user to add a restaurant they have visited
         *
         *  In the future this will create an intent for a form activity that allows the user to give information about their restaurant
         * */
        FloatingActionButton fab = findViewById(R.id.fab_visited);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something for now, probably add a generic restaurant to the listview
            }
        });


    }

    /**
     * Creates the menu with oncreate.
     *
     * @param menu
     * @return bool on whether it was successful.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    /**
     * Logic for when menu is pressed.
     *
     * @param item
     * @return bool on whether it was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded item data into the database. For debugging purposes only.
     */
    private void insertItem() {
        // Fetch dummy image
        Bitmap dummyImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.jakes_pizza);
        // Convert dummy image to bytes so it can be written to db
        byte[] dummyImageInBytes = ImageUtils.getBytes(dummyImage);

        ContentValues values = new ContentValues();
        values.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME, "Jakes Pizza Shack");
        values.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ADDRESS, "101 Moonbase, Moon");
        values.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NOTE, "It was too good I died");
        values.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE, dummyImageInBytes);
        Uri newUri = getContentResolver().insert(RestaurantContract.RestaurantEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Successfully inserted dummy data.");
    }

    /**
     * Initializes the cursor with the activity.
     *
     * @param i
     * @param bundle
     * @return cursor
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                RestaurantContract.RestaurantEntry._ID,
                RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME,
                RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ADDRESS,
                RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NOTE,
                RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE };

        return new CursorLoader(this,
                RestaurantContract.RestaurantEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    /*
     * Stubs for methods to be implemented later. Will need for views.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    /*
     * Stubs for methods to be implemented later. Will need for views.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
