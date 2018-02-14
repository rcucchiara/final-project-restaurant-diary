package com.example.android.restaurantdiary.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the restaurantdiary app data.
 */

public final class RestaurantContract {

    /** Empty constructor */
    private RestaurantContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.restaurantdiary";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.restaurantdiary/restaurant/ is a valid path for
     * looking at item data. content://com.example.android.restaurantdiary/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_VISITED_RESTAURANT = "visitedrestaurant";

    /** Prospective Restaurant URL */
    public static final String PATH_PROSPECTIVE_RESTAURANT = "prospectiverestaurant";

    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single restaurant.
     */
    public static final class VisitedRestaurantEntry implements BaseColumns {

        /** The content URI to access the restaurant data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VISITED_RESTAURANT);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of restaurants.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VISITED_RESTAURANT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single restaurant.
         */
        public static final String CONTENT_RESTAURANT_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VISITED_RESTAURANT;

        /** Name of database table for restaurants */
        public final static String TABLE_NAME = "visitedrestaurant";

        /**
         * Unique ID number for the restaurant (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_NAME ="name";

        /**
         * Address of the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_ADDRESS = "address";

        /**
         * Phone number for the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_PHONE = "phone";

        /**
         * Note for the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_NOTE = "note";

        /**
         * Main image for the restaurant.
         *
         * Type: Image
         */
        public final static String COLUMN_RESTAURANT_IMAGE = "image";

    }

    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single restaurant.
     */
    public static final class ProspectiveRestaurantEntry implements BaseColumns {

        /** The content URI to access the restaurant data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PROSPECTIVE_RESTAURANT);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of restaurants.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROSPECTIVE_RESTAURANT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single restaurant.
         */
        public static final String CONTENT_RESTAURANT_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROSPECTIVE_RESTAURANT;

        /** Name of database table for restaurants */
        public final static String TABLE_NAME = "prospectiverestaurant";

        /**
         * Unique ID number for the restaurant (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_NAME ="name";

        /**
         * Address of the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_ADDRESS = "address";

        /**
         * Phone number for the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_PHONE = "phone";

        /**
         * Note for the restaurant.
         *
         * Type: TEXT
         */
        public final static String COLUMN_RESTAURANT_NOTE = "note";

        /**
         * Main image for the restaurant.
         *
         * Type: Image
         */
        public final static String COLUMN_RESTAURANT_IMAGE = "image";

    }
}
