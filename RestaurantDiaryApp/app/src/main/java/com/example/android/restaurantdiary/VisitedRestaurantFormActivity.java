package com.example.android.restaurantdiary;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.restaurantdiary.data.RestaurantContract.VisitedRestaurantEntry;
import com.example.android.restaurantdiary.utils.ImageUtils;

import static com.example.android.restaurantdiary.AiSentimentCalculator.AiSentiment;


public class VisitedRestaurantFormActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the restaurant data loader */
    private static final int EXISTING_RESTAURANT_LOADER = 0;

    /** EditText field to enter the restaurant's name */
    private EditText mNameEditText;

    /** EditText field to enter the restaurant's address */
    private EditText mAddressEditText;

    /** EditText field for note */
    private EditText mNoteEditText;

    /** EditText field for phone */
    private EditText mPhoneEditText;

    /** ImageView field */
    private ImageView mImageView;

    /** Content URI for the existing restaurant (null if it's a new restaurant) */
    private Uri mCurrentRestaurantUri;

    /** Boolean flag that keeps track of whether the restaurant
     *  has been edited (true) or not (false) */
    private boolean mRestaurantHasChanged = false;
    // stores neutral image
    private Bitmap mNeutralImage;
    // stores positive image
    private Bitmap mPositiveImage;
    // stores negative image
    private Bitmap mNegativeImage;
    // stores the score that the ai returns
    private Double mSentiment;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mRestaurantHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mRestaurantHasChanged = true;
            return false;
        }
    };

    /**
     * onCreate method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new restaurant or editing an existing one.
        Intent intent = getIntent();
        mCurrentRestaurantUri = intent.getData();

        // If the intent DOES NOT contain a restaurant content URI, then we know that we are
        // creating a new restaurant.
        if (mCurrentRestaurantUri == null || intent.getStringExtra("NameOfCallingClass") != null) {
            // This is a new restaurant, so change the app bar to say "Add a restaurant"
            setTitle(getString(R.string.editor_activity_title_new_restaurant));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a restaurant that hasn't been created yet.)
            invalidateOptionsMenu();

            mCurrentRestaurantUri = null;
        } else {
            // Otherwise this is an existing restaurant, so change app bar to say "Edit restaurant"
            setTitle(getString(R.string.editor_activity_title_edit_restaurant));

            // Initialize a loader to read the restaurant data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_RESTAURANT_LOADER, null, this);
        }

        // Set up icons
        mNeutralImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_neutral);
        mPositiveImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_like);
        mNegativeImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_dislike);

        mNameEditText = (EditText) findViewById(R.id.form_visited_name);
        mAddressEditText = (EditText) findViewById(R.id.form_visited_address);
        mPhoneEditText = (EditText) findViewById(R.id.form_visited_phone);
        mNoteEditText = (EditText) findViewById(R.id.form_visited_note);
        mImageView = (ImageView) findViewById(R.id.image);

        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phoneNumber");
        String rating = intent.getStringExtra("rating");

        mNameEditText.setText(name);
        mAddressEditText.setText(address);
        mPhoneEditText.setText(phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mAddressEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and save restaurant into database.
     */
    private void saveRestaurant() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String addressString = mAddressEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String noteString = mNoteEditText.getText().toString().trim();



        // Check if this is supposed to be a new restaurant
        // and check if all the fields in the editor are blank
        if (mCurrentRestaurantUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(addressString) &&
                TextUtils.isEmpty(phoneString) &&
                TextUtils.isEmpty(noteString) ) {
            // Since no fields were modified, we can return early without creating a new restaurant.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Context context = getApplicationContext();
            Toast.makeText(context, getString(R.string.incomplete_form), Toast.LENGTH_SHORT).show();
        } else {
            AsyncSaveTask task = new AsyncSaveTask();
            task.execute(nameString,addressString,phoneString,noteString);

            finish();
        }
    }

    // creates an new loader when the activity is created and initializes the loader
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                VisitedRestaurantEntry._ID,
                VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME,
                VisitedRestaurantEntry.COLUMN_RESTAURANT_ADDRESS,
                VisitedRestaurantEntry.COLUMN_RESTAURANT_NOTE,
                VisitedRestaurantEntry.COLUMN_RESTAURANT_PHONE,
                VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE };

        return new CursorLoader(this,
                mCurrentRestaurantUri,
                projection,
                null,
                null,
                null);
    }

    // gets called when loader finishes and cleans up
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of restaurant attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME);
            int addressColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_ADDRESS);
            int phoneColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_PHONE);
            int noteColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_NOTE);
            int imageColumnIndex = cursor.getColumnIndex(VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            byte[] restaurantImage = cursor.getBlob(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAddressEditText.setText(address);
            mPhoneEditText.setText(phone);
            mNoteEditText.setText(note);
            if (restaurantImage != null) {
                Bitmap decodedImageBitmap = ImageUtils.getImage(restaurantImage);
                mImageView.setImageBitmap(decodedImageBitmap);
            }

        }
    }

    // resets loader values
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Update the views on the screen with the values from the database
        mNameEditText.setText("");
        mAddressEditText.setText("");
        mPhoneEditText.setText("");
        mNoteEditText.setText("");
        mImageView.setImageDrawable(null);
    }

    /**
     * Prompt the user to confirm that they want to delete this restaurant.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the restaurant.
                deleteRestaurant();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the restaurant.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the restaurant in the database.
     */
    private void deleteRestaurant() {
        // Only perform the delete if this is an existing restaurant.
        if (mCurrentRestaurantUri != null) {
            // Call the ContentResolver to delete the restaurant at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentRestaurantUri
            // content URI already identifies the restaurant that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentRestaurantUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_restaurant_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_restaurant_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentRestaurantUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                saveRestaurant();
                // Exit activity
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mRestaurantHasChanged) {
                    NavUtils.navigateUpFromSameTask(VisitedRestaurantFormActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(VisitedRestaurantFormActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mRestaurantHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // this make the when you save the dummy data asynchronous
    // you need to do this b/c you dont want to stall the gui thread
    private class AsyncSaveTask extends AsyncTask<String, Void, ContentValues> {
        private final String LOG_TAG = AsyncSaveTask.class.getSimpleName();

        @Override
        protected ContentValues doInBackground(String... textsToAnalyse) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //textView.setText("what is happening inside a thread - we are running Watson AlchemyAPI");
                }
            });

            // Create a ContentValues object where column names are the keys,
            // and restaurant attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_NAME, textsToAnalyse[0]);
            values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_ADDRESS, textsToAnalyse[1]);
            values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_PHONE, textsToAnalyse[2]);
            values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_NOTE, textsToAnalyse[3]);

            // get sentiment
            mSentiment = AiSentiment(textsToAnalyse[3]);

            return values;
        }

        //setting the value of UI outside of the thread
        @Override
        protected void onPostExecute(ContentValues values) {
            // set up images
            if (mSentiment <= .25 && mSentiment >= -0.25) // neutral
                values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE, ImageUtils.getBytes(mNeutralImage));
            else if (mSentiment > .25) // positive
                values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE, ImageUtils.getBytes(mPositiveImage));
            else if (mSentiment < -0.25) // negative
                values.put(VisitedRestaurantEntry.COLUMN_RESTAURANT_IMAGE, ImageUtils.getBytes(mNegativeImage));

            // If the price is not provided by the user, don't try to parse the string into an
            // integer value. Use 0 by default.
            // Determine if this is a new or existing restaurant by checking if mCurrentRestaurantUri is null or not
            if (mCurrentRestaurantUri == null) {
                // This is a NEW restaurant, so insert a new restaurant into the provider,
                // returning the content URI for the new restaurant.
                Uri newUri = getContentResolver().insert(VisitedRestaurantEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(VisitedRestaurantFormActivity.this, getString(R.string.editor_insert_restaurant_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(VisitedRestaurantFormActivity.this, getString(R.string.editor_insert_restaurant_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING restaurant, so update the restaurant with content URI: mCurrentRestaurantUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentRestaurantUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentRestaurantUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(VisitedRestaurantFormActivity.this, getString(R.string.editor_update_restaurant_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(VisitedRestaurantFormActivity.this, getString(R.string.editor_update_restaurant_succesful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
