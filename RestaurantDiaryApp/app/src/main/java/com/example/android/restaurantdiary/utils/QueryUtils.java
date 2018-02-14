package com.example.android.restaurantdiary.utils;

/**
 * Used for fetching yelp data from the yelp API.
 *
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/** Helper methods related to requesting and receiving Yelp data from Yelp API. */
public final class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Client Key, issued by Yelp upon registering app**/
    private static final String authKey = "vC80EX7PMl3g4JYrLlvzng";

    /** Client Secret, issued by Yelp */
    private static final String authSecret = "fIFKCvMP970Sl0jTLToCVcRJvV5twrWCFCAJLwUzgiM1dUaylgUyZTZuoD9Wrlhg";

    /** Authorization Key Request Parameter  **/
    private static final String authGrantType = "client_credentials";

    /** Authorization Key Token request URL,
     * with bearerToken and Token type return type variables **/
    private static final String tokenURL = "https://api.yelp.com/oauth2/token";
    public static String bearerToken= "";
    public static String tokenType = "";

    /** Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed). */
    private QueryUtils() {
    }

    /** Query the YELP API and return a list of {@link Restaurant} objects.
     * First requesting Token through Get Request and credentials
     * Then requesting the JSON data through the Post request to Yelp API*/
    public static List<Restaurant> fetchYelpData(String requestUrl) {

        /** Create URL objects to send requests through**/
        URL searchUrl = createUrl(requestUrl);
        URL authUrl = createUrl(tokenURL);

        String jsonResponse = null;

        /** Perform HTTP request to the URL and receive a JSON response back to set the Bearer Token**/
        if(bearerToken == "") {
            try {
                jsonResponse = sendPOSTHttpRequest(authUrl);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making HTTP POST request.", e);
            }

            /** on successful post request, extract results from json**/
            extractTokenJSONResults(jsonResponse);
        }
        /** Perform HTTP request to the URL and receive a JSON response back of restaurant query data**/
        try {
            jsonResponse = sendGETHttpRequest(searchUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP GET request.", e);
        }

        /** Extract relevant fields from the JSON response and create a list of {@link YelpBusiness}s**/
        List<Restaurant> yelpBusinesses = extractRestaurantFromJson(jsonResponse);

        // Return the list of {@link YelpBusiness}s
        return yelpBusinesses;
    }

    /**
     * Returns new URL object from the given string URL.
     *
     * @param stringUrl
     * @return URL created
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

    /**
     * Returns JSON body for Bearer Token
     *
     * @param url
     * @return JSON response created
     */
    private static String sendPOSTHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader reader = null;

        /** URL  to send over outBuffer
          Create data variable for sent values to server **/
        String data = URLEncoder.encode("grant_type", "UTF-8")
                + "=" + URLEncoder.encode(authGrantType, "UTF-8");

        data += "&" + URLEncoder.encode("client_id", "UTF-8")
                + "=" + URLEncoder.encode(authKey, "UTF-8");

        data += "&" + URLEncoder.encode("client_secret", "UTF-8")
                + "=" + URLEncoder.encode(authSecret, "UTF-8");

        /** Send Request Parameters and properties**/
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            /**Confirm we're sending request body.**/
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            urlConnection.connect();

            /** output stream to send body data for request **/
            outputStream = urlConnection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "POST Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving authorization token JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the sendGETHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     *
     * @param url
     * @return JSON response created
     */
    private static String sendGETHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        /** Send Request Parameters and properties**/
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", tokenType + " " + bearerToken);
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
                // the sendGETHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     *
     * @param inputStream
     * @return whole JSON response
     */
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

    /**
     * Extract the token and token type from json results
     * and change global variables
     * @param authJSON
     * @return bearer token and token type
     */
    private static void extractTokenJSONResults (String authJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(authJSON)) {
            return;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(authJSON);
            bearerToken = baseJsonResponse.getString("access_token");
            tokenType = baseJsonResponse.getString("token_type");
        } catch (JSONException ex) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing token JSON results", ex);
        }
    }

    /**
     * Return a list of {@link Restaurant} objects that has been built up from
     * parsing the given JSON response.
     * @param yelpJSON
     * @return list of restaurants
     */
    private static List<Restaurant> extractRestaurantFromJson(String yelpJSON) {
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
                String city = loc.getString("city");
                String state = loc.getString("state");

                address += (". " + city + ", " +  state);
                String rating = businessListObj.getString("rating");

                // Create a new {@link YelpBusiness} object with the location, time,
                // and url from the JSON response.
                Restaurant yelpBusiness = new Restaurant(title, type, address, phone, rating);
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
