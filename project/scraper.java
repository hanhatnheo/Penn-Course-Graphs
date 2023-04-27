package project;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class scraper {
    public static Document getDocument(String url) {
        Document document = null;
        try {
            document =  Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        } catch (IOException e) {
            e.printStackTrace();
            // handle error
        }
        return document;
    }

    public static HashMap<String, String> getSubject() {
        Document catalog = getDocument("https://catalog.upenn.edu/courses/");
        HashMap<String, String> subjects = new HashMap<>();
        Elements catalogPage = catalog.select("div.az_sitemap > ul > li");
        for (Element subject: catalogPage) {
            String url = "https://catalog.upenn.edu" + subject.select("a").attr("href");
            String subjectCode = subject.text();
            subjects.put(subjectCode, url);
        }
        return subjects;
    }

    public static void getCourse() {
        HashMap<String, String> subjects = getSubject();

    }

    public static void main(String[] args) {
        getSubject();
    }

}
