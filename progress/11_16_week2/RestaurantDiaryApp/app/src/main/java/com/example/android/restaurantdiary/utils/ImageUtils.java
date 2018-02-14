package com.example.android.restaurantdiary.utils;

/**
 * ImageUtils is a static class meant to be used for converting Bitmap to type byte[] for storing
 * in sqlite.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

    /**
     * Used for converting bitmap to bytes.
     *
     * @param bitmap is the image
     * @return image encoded in bytes
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Used for converting bytes to bitmap.
     *
     * @param imageBytes is the image in bytes.
     * @return image decoded to Bitmap.
     */
    public static Bitmap getImage(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

}