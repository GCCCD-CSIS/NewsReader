package edu.gcccd.csis;

import com.google.gson.Gson;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class NewsReader {
    private static final String API_KEY = "36eb818bd02c4f46b4f942bde7abf139";
    private static final String SOURCES = "https://newsapi.org/v2/sources?apiKey=";
    private static final String CONTENT = "https://newsapi.org/v2/everything?apiKey=%s&sources=%s&language=%s";
    private static final String TOPIC = "technology";
    private static final String LANGUAGE = "en";
    private static final String PAGE = "<html><head><meta charset=\"UTF-8\"><title>GCCCD CSIS Tech News Reader</title></head><body>%s%s</body></html>";
    private static final String HDR = "<h1><img height=\"128\" src=\"https://www.grossmont.edu/faculty-staff/creative-services/Grossmont%20College%20Logos/GC_Shield_RGB_Black_sm.jpg\"/>GCCCD CSIS Tech News Reader</h1>";
    private static final String STORY = "<div><img src=\"%s\" align=\"right\" height=\"128\"><p>%s</p><h2>%s</h2><p>%s</p><br clear=\"all/\"><a href=\"%s\" target=\"story\">more ...</a><hr/></div>";

    private static final List<Article> stories = new Vector<>();

    private static String getRAWData(final String s) throws Exception {
        final URL url = new URL(s);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String content = "";
        String line;
        while (null != (line = reader.readLine())) {
            content += line;
        }
        return content;
    }

    public static void main(final String[] args) throws Exception {
        final List<Thread> threads = new ArrayList<>();
        final String s = getRAWData(SOURCES + API_KEY);
        WSResponse wsResponse = new Gson().fromJson(s, WSResponse.class);
        for (final Source source : wsResponse.sources) {
            if (source.category.equals(TOPIC) && source.language.equals(LANGUAGE)) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            String s = getRAWData(String.format(CONTENT, API_KEY, source.id, LANGUAGE));
                            WSResponse ws = new Gson().fromJson(s, WSResponse.class);
                            Collections.addAll(stories, ws.articles);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                threads.add(t);
            }
        }
        for (Thread t : threads) {
            t.join();
        }
        try {
            Desktop.getDesktop().browse(buildHtmlPage().toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File buildHtmlPage() throws IOException {
        final File htmlFile = File.createTempFile("news", ".html");
        String s = "";
        for (final Article a : stories) {
            s += String.format(STORY,  a.urlToImage, a.publishedAt, a.title, a.description, a.url);
        }
        new FileOutputStream(htmlFile).write(String.format(PAGE, HDR, s).getBytes("UTF-8"));
        return htmlFile;
    }
}
