package com.example.android.newsapp;
import android.text.TextUtils;
import android.util.Log;
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


public final class QueryUtils {

    // LOG String
    public static final String LOG_TAG = QueryUtils.class.getName();

    // Empty constructor
    private QueryUtils() {
    }


    // I. Creating new URL object from the given string URL.
     private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

     // II. Making an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // Handle the exception
            Log.e(LOG_TAG, "Problem with getting News JSON results");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // II.a Convert the InputStream into a String which contains the whole JSON response from the server.

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

    // III. Parsing JSON response and creating List news
    public static List<News> extractNews(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<News> news = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 1; i < results.length(); i++) {
                JSONObject article = results.getJSONObject(i);
                String title = article.getString("webTitle");
                String section = article.getString("sectionName");
                String date = article.getString("webPublicationDate");
                String url = article.getString("webUrl");

                news.add(new News(title, section, date, url));
            }
        } catch (JSONException e) {
            // If an error is thrown when parsing JSON
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return news;
    }

    // IV. Summarizes all the method into 1
    public static List<News> fetchNewsdata(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResp = null;
        try {
            jsonResp = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with getting News JSON results");
        }
        List<News> news = extractNews(jsonResp);
        return news;
    }

}
