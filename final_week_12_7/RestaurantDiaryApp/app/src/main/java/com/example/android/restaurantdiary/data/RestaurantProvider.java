package com.example.android.restaurantdiary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.restaurantdiary.data.RestaurantContract.VisitedRestaurantEntry;
import com.example.android.restaurantdiary.data.RestaurantContract.ProspectiveRestaurantEntry;

/**
 * {@link ContentProvider} for restaurantdiary app.
 */

public class RestaurantProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = RestaurantProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the visited restaurant table */
    private static final int VISITED_RESTAURANTS = 100;

    /** URI matcher code for the content URI for a single restaurant in the visited
     * restaurant table */
    private static final int VISITED_RESTAURANT_ID = 101;

    /** URI matcher code for the content URI for the prospective restaurant table */
    private static final int PROSPECTIVE_RESTAURANTS = 102;

    /** URI matcher code for the content URI for a single restaurant in the prospective
     * restaurant table */
    private static final int PROSPECTIVE_RESTAURANT_ID = 103;



    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Uri matcher initialization
    static {
        // URI used to provide access to MULTIPLE rows of the visited restaurants table.
        sUriMatcher.addURI(RestaurantContract.CONTENT_AUTHORITY,
                RestaurantContract.PATH_VISITED_RESTAURANT, VISITED_RESTAURANTS);
        // URI used to provide access to a single row of the visited restaurants table.
        sUriMatcher.addURI(RestaurantContract.CONTENT_AUTHORITY,
                RestaurantContract.PATH_VISITED_RESTAURANT + "/#", VISITED_RESTAURANT_ID);
        // URI used to provide access to MULTIPLE rows of the prospective restaurants table.
        sUriMatcher.addURI(RestaurantContract.CONTENT_AUTHORITY,
                RestaurantContract.PATH_PROSPECTIVE_RESTAURANT, PROSPECTIVE_RESTAURANTS);
        // URI used to provide access to a single row of the prospective restaurants table.
        sUriMatcher.addURI(RestaurantContract.CONTENT_AUTHORITY,
                RestaurantContract.PATH_PROSPECTIVE_RESTAURANT + "/#", PROSPECTIVE_RESTAURANT_ID);
    }

    /** Database helper object */
    private RestaurantDbHelper mDBHelper;

    /**
     * Initializes the DBhelper
     *
     * @return bool on successful creation of helper
     */
    @Override
    public boolean onCreate() {
        mDBHelper = new RestaurantDbHelper((getContext()));
        return true;
    }

    /**
     * Returns the cursor with correct positioning in the db when it is queried.
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANTS:
                cursor = database.query(VisitedRestaurantEntry.TABLE_NAME,
                        projection, selection, selectionArgs,null, null, sortOrder);
                break;

            case VISITED_RESTAURANT_ID:
                selection = VisitedRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(VisitedRestaurantEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case PROSPECTIVE_RESTAURANTS:
                cursor = database.query(ProspectiveRestaurantEntry.TABLE_NAME,
                        projection, selection, selectionArgs,null, null, sortOrder);
                break;

            case PROSPECTIVE_RESTAURANT_ID:
                selection = VisitedRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProspectiveRestaurantEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Inserts the restaurant.
     *
     * @param uri
     * @param contentValues
     * @return Uri with the newly added ID appended at the end.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANTS:
                return insertRestaurant(uri, contentValues);
            case PROSPECTIVE_RESTAURANTS:
                return insertRestaurant(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Inserts the restaurant into the db using the mDBHelper.
     *
     * @param uri
     * @param values
     * @return Uri with the newly added ID.
     */
    private Uri insertRestaurant(Uri uri, ContentValues values) {
        String columnName = null;
        String tableName = null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANTS:
                columnName = values.getAsString(VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME);
                tableName = VisitedRestaurantEntry.TABLE_NAME;
                break;
            case PROSPECTIVE_RESTAURANTS:
                columnName = values.getAsString(ProspectiveRestaurantEntry.COLUMN_RESTAURANT_NAME);
                tableName = ProspectiveRestaurantEntry.TABLE_NAME;
                break;
        }

        if (columnName == null) {
            throw new IllegalArgumentException("Restaurant requires a name");
        }

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        long id = database.insert(tableName, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the existing entry.
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return number of rows updated.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case VISITED_RESTAURANTS:
                return updateRestaurant(uri, contentValues, selection, selectionArgs);
            case VISITED_RESTAURANT_ID:
                selection = VisitedRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRestaurant(uri, contentValues, selection, selectionArgs);
            case PROSPECTIVE_RESTAURANTS:
                return updateRestaurant(uri, contentValues, selection, selectionArgs);
            case PROSPECTIVE_RESTAURANT_ID:
                selection = ProspectiveRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateRestaurant(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Helper method for updating the entry.
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return Number of rows updated.
     */
    private int updateRestaurant(Uri uri, ContentValues values,
                                 String selection, String[] selectionArgs) {
        String tableName = null;
        String columnRestaurantName = null;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANT_ID:
                columnRestaurantName = values.getAsString(VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME);
                tableName = VisitedRestaurantEntry.TABLE_NAME;
                break;
            case PROSPECTIVE_RESTAURANT_ID:
                columnRestaurantName = values.getAsString(ProspectiveRestaurantEntry.COLUMN_RESTAURANT_NAME);
                tableName = ProspectiveRestaurantEntry.TABLE_NAME;
                break;
        }
        if (values.containsKey(columnRestaurantName)) {
            String name = values.getAsString(columnRestaurantName);
            if (name == null) {
                throw new IllegalArgumentException("Restaurant requires a Name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(tableName, values,
                selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Deletes the entry.
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return Number of rows deleted.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANTS:
                rowsDeleted = database.delete(VisitedRestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case VISITED_RESTAURANT_ID:
                selection = VisitedRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(VisitedRestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PROSPECTIVE_RESTAURANTS:
                rowsDeleted = database.delete(ProspectiveRestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PROSPECTIVE_RESTAURANT_ID:
                selection = ProspectiveRestaurantEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProspectiveRestaurantEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Method to find type.
     * @param uri
     * @return Type of entry.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VISITED_RESTAURANTS:
                return VisitedRestaurantEntry.CONTENT_LIST_TYPE;
            case VISITED_RESTAURANT_ID:
                return VisitedRestaurantEntry.CONTENT_RESTAURANT_TYPE;
            case PROSPECTIVE_RESTAURANTS:
                return ProspectiveRestaurantEntry.CONTENT_LIST_TYPE;
            case PROSPECTIVE_RESTAURANT_ID:
                return ProspectiveRestaurantEntry.CONTENT_RESTAURANT_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
