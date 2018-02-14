package com.example.android.restaurantdiary.utils;

/**
 * Created by dave on 11/3/17.
 */
import android.text.TextUtils;
import android.util.Log;

import com.example.android.restaurantdiary.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/** Helper methods related to requesting and receiving Yelp data from Yelp API. */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed). */
    private QueryUtils() {
    }

    /** Query the YELP API and return a list of {@link Restaurant} objects. */
    public static List<Restaurant> fetchYelpData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link YelpBusiness}s
        List<Restaurant> yelpBusinesses = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link YelpBusiness}s
        return yelpBusinesses;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /** Make an HTTP request to the given URL and return a String as the response. */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            //Added Code for Bearer Token, in Future we should POST query for this
            urlConnection.setRequestProperty("Authorization", "Bearer S7jiIlPapAB5Wg6hIVVNN0VSCa8sLDBpymQJ91fbB_q3N5ecE91MBLXXg6-sNCMVXEd8v9abfhzra1Nef07NINNPf1iF8Kb7KMF7-8Ie5zPlZ1zzwzn_7nSnJWEDWnYx");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Yelp JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /** Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server. */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /** Return a list of {@link Restaurant} objects that has been built up from
     * parsing the given JSON response. */
    private static List<Restaurant> extractFeatureFromJson(String yelpJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(yelpJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding yelpBusinesses to
        List<Restaurant> yelpBusinesses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string , so this already a response
            //JSONObject baseJsonObject = new JSONObject(yelpJSON);
            JSONObject baseJsonResponse = new JSONObject(yelpJSON);


            // Extract the JSONArray associated with the key called "businesses",
            // which represents a list of results (or businesses).
            //JSONObject response = baseJsonObject.getJSONObject("response");
            JSONArray businessArray = baseJsonResponse.getJSONArray("businesses");
            //JSONArray businessArray = response.getJSONArray("businesses");

            // For each business in the businessArray, create an {@link YelpBusiness} object
            for (int i = 0; i < businessArray.length(); i++) {

                // Get a single yelpBusiness at position i within the list of yelpBusinesses
                JSONObject businessListObj = businessArray.getJSONObject(i);

                String title = businessListObj.getString("name");

                //We don't actually need categories, we need title inside of alias inside of categories
                JSONArray categories = businessListObj.getJSONArray("categories");
                JSONObject alias = categories.getJSONObject(0);
                String type = alias.getString("alias");

                String phone = businessListObj.getString("display_phone");

                //We don't actually need location, we need address inside location
                JSONObject loc = businessListObj.getJSONObject("location");
                String address = loc.getString("address1");

                String rating = businessListObj.getString("rating");

                // Create a new {@link YelpBusiness} object with the location, time,
                // and url from the JSON response.
                Restaurant yelpBusiness = new Restaurant(title, type, phone, address, rating);
                //YelpBusiness yelpBusiness = new YelpBusiness(author, section, location, url);

                // Add the new {@link YelpBusiness} to the list of yelpBusinesses.
                yelpBusinesses.add(yelpBusiness);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of yelpBusinesses
        return yelpBusinesses;
    }

}
