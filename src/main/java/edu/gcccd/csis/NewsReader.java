package edu.gcccd.csis;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;

public class NewsReader {
    private static final String s = "https://newsapi.org/v1/articles?source=ars-technica&sortBy=top&apiKey=36eb818bd02c4f46b4f942bde7abf139";

    private static String readRawData(final URL url)throws IOException {
        final URLConnection urlConnection = url.openConnection();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String line;
        StringBuilder content = new StringBuilder();

        while (null != (line = bufferedReader.readLine())) {
            content.append(line);
            content.append("\n");
        }
        bufferedReader.close();
        return content.toString();
    }

    public static void main(final String[] args) throws IOException {
        final URL url = new URL(s);
        final String raw = readRawData(url);
        final NewsFeed feed = new Gson().fromJson(raw, NewsFeed.class);
        for (final NewsStory article : feed.articles) {
            System.out.println(article);
        }
    }
}
