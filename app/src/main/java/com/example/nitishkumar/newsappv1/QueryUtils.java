package com.example.nitishkumar.newsappv1;
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

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    public static List<NewsData> fetchNewsData(String requestUrl)
    {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return extractNewsFromJson(jsonResponse);
    }

    private static URL createUrl (String stringUrl)
    {
        URL url = null;
        try
        {
            url = new URL(stringUrl);
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException
    {
        String jsonResponse = "";
        if (url == null)
        {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e)
        {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (inputStream != null)
            {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null)
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null)
            {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsData> extractNewsFromJson(String NewsJSON)
    {
        if (TextUtils.isEmpty(NewsJSON))
        {
            return null;
        }
        List<NewsData> newsDataSet = new ArrayList<>();
        try {
            JSONObject baseJSONObject = new JSONObject(NewsJSON);
            JSONObject responseJSONObject = baseJSONObject.getJSONObject("response");
            JSONArray resultJSONArray = responseJSONObject.getJSONArray("results");

            for (int i = 0; i < resultJSONArray.length(); i++)
            {
                JSONObject currentNewsJSONObject = resultJSONArray.getJSONObject(i);
                String newsType = currentNewsJSONObject.getString("sectionName");
                String newsdateTimeStamp = currentNewsJSONObject.getString("webPublicationDate");
                 newsdateTimeStamp = newsdateTimeStamp.split("T")[0];
                String newswebsiteURL = currentNewsJSONObject.getString("webUrl");
                JSONObject fieldsJSONObject = currentNewsJSONObject.getJSONObject("fields");
                String newsHeadline = fieldsJSONObject.getString("headline");
                String newsThumbnail = fieldsJSONObject.getString("thumbnail");
                JSONArray tagsJSONArray = currentNewsJSONObject.getJSONArray("tags");
                String newsAuthor = "";
                for (int j = 0; j < tagsJSONArray.length(); j++)
                {
                    newsAuthor = "";
                    JSONObject currentNewsTag = tagsJSONArray.getJSONObject(j);
                    String currentNewsAuthor = currentNewsTag.getString("firstName");
                    currentNewsAuthor += " " + currentNewsTag.getString("lastName");

                    newsAuthor += currentNewsAuthor + ",";
                }
                NewsData newsData = new NewsData(newsThumbnail, newsHeadline, newsdateTimeStamp, newsAuthor, newsType, newswebsiteURL);
                newsDataSet.add(newsData);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem in Parsing JSON.", e);
        }

        return newsDataSet;
    }
}