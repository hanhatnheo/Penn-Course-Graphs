package project;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.*;

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

    public static void getCourses() {
        HashMap<String, String> subjects = getSubject();
        for (Map.Entry<String, String> subjectEntry : subjects.entrySet()) {
            Document subject = getDocument(subjectEntry.getValue());
            Elements courses = subject.select(".courseblock");
            for (Element course : courses) {
                String title = course.select("strong").text();
                String[] parts = title.split("\\s+(?=[A-Z])");
                String courseCode = parts[0];
                String courseName = title.replace(courseCode + " ", "");
                Elements courseBlocks = course.select(".courseblockextra");
                String description = courseBlocks.first().text();
                int count = 0;
                for (Element block : courseBlocks) {
                    if (count > 1) {
                        if (block.text().contains("Prerequisite: ")) {
                            String[] prerequisites = block.text().split(" AND");
                            for (String prereq : prerequisites) {
                                String[] substrings = prereq.split("(?<=\\))\\s*(AND|OR)\\s*|\\s*(AND|OR)\\s*(?=\\()|\\s+");
                            }
                        }
                        else {
                            description = description + " " + block.text();
                        }
                    }
                    count++;
                }

            }
        }

    }

    public static void main(String[] args) {
        getSubject();
        getCourses();
    }

}
