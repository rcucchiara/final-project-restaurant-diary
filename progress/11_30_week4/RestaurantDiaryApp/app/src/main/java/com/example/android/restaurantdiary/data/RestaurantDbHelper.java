package com.example.android.restaurantdiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.restaurantdiary.data.RestaurantContract.VisitedRestaurantEntry;
import com.example.android.restaurantdiary.data.RestaurantContract.ProspectiveRestaurantEntry;

/**
 * Database helper for the restaurantdiary app. Manages database creation and version management.
 */
public class RestaurantDbHelper extends SQLiteOpenHelper {

    /** Logger tag for the Restaurant DB class */
    public static final String LOG_TAG = RestaurantDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "restaurants.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructs a new instance of {@link RestaurantDbHelper}.
     * @param context of the app
     */
    public RestaurantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     *
     * @param db of type SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the restaurants table
        String SQL_CREATE_VISITED_RESTAURANTS_TABLE =  "CREATE TABLE " +
                VisitedRestaurantEntry.TABLE_NAME + " ("
                + VisitedRestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME + " TEXT NOT NULL, "
                + VisitedRestaurantEntry.COLUMN_RESTAURANT_ADDRESS + " TEXT , "
                + VisitedRestaurantEntry.COLUMN_RESTAURANT_PHONE + " TEXT , "
                + VisitedRestaurantEntry.COLUMN_RESTAURANT_NOTE + " TEXT , "
                + VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE + " BLOB ); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_VISITED_RESTAURANTS_TABLE);

        // Create a String that contains the SQL statement to create the restaurants table
        String SQL_CREATE_PROSPECTIVE_RESTAURANTS_TABLE =  "CREATE TABLE " +
                ProspectiveRestaurantEntry.TABLE_NAME + " ("
                + ProspectiveRestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProspectiveRestaurantEntry.COLUMN_RESTAURANT_NAME + " TEXT NOT NULL, "
                + ProspectiveRestaurantEntry.COLUMN_RESTAURANT_ADDRESS + " TEXT , "
                + ProspectiveRestaurantEntry.COLUMN_RESTAURANT_PHONE + " TEXT , "
                + ProspectiveRestaurantEntry.COLUMN_RESTAURANT_NOTE + " TEXT , "
                + ProspectiveRestaurantEntry.COLUMN_RESTAURANT_IMAGE + " BLOB ); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PROSPECTIVE_RESTAURANTS_TABLE);
    }

    /**
     * Stub for onUpgrade. To be used when upgrading the db.
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VisitedRestaurantEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProspectiveRestaurantEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}