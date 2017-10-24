package edu.gcccd.csis;

public class NewsStory {
    String author;
    String title;
    String description;
    String url;

    @Override
    public String toString() {
        return String.format("%s : %s\n %s\n-%s\n\n",author,title,description,url);
    }
}
