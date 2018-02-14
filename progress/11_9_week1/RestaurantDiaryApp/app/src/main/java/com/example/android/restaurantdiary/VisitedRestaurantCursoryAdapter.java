package com.example.android.restaurantdiary;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by jake on 11/8/17.
 */

public class VisitedRestaurantCursoryAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link VisitedRestaurantCursoryAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public VisitedRestaurantCursoryAdapter(Context context, Cursor c){
        super(context,c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /**TODO
         * once gui is merged uncomment point to layout might have to change
         * @para R.layout.list_item
         *
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
         */
        return null;
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO this is waiting on gui
        return;
    }
}
