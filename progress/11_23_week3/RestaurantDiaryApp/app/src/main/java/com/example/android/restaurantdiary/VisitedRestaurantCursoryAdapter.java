package com.example.android.restaurantdiary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.restaurantdiary.data.RestaurantContract.VisitedRestaurantEntry;
import com.example.android.restaurantdiary.utils.ImageUtils;

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
        // Inflate a list item view using the layout specified in restaurant_list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.restaurant_list_item, parent, false);
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
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.restaurant_name);
        TextView addressTextView = (TextView) view.findViewById(R.id.restaurant_address);
        ImageView pictureImageView = (ImageView) view.findViewById(R.id.restaurant_image);
        TextView noPicTextView = (TextView) view.findViewById(R.id.restaurant_no_picture);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME);
        int addressColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_ADDRESS);
        int pictureColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE);

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        String itemAddress = cursor.getString(addressColumnIndex);
        byte[] itemImage = cursor.getBlob(pictureColumnIndex);

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        addressTextView.setText(itemAddress);

        // If the item breed is empty string or null, then use some default text
        // that says "No Image touch to take one", so the TextView isn't blank.
        if (itemImage == null) {
            noPicTextView.setText(R.string.no_available_image);
        }else {
            Bitmap decodedImageBitmap = ImageUtils.getImage(itemImage);
            pictureImageView.setImageBitmap(decodedImageBitmap);
        }

        return;
    }
}
