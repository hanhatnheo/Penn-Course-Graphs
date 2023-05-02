package project;

import java.io.IOException;
import java.net.URL;
import java.util.*;
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

    public static JSONObject getCourses() {
        HashMap<String, String> subjects = getSubject();
        JSONObject courseGraph = new JSONObject();
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
                JSONObject courseObj = new JSONObject();
                courseObj.put("code", courseCode);
                courseObj.put("name", courseName);
                for (Element block : courseBlocks) {
                    if (count > 1) {
                        boolean isAnds = false;
                        if (block.text().contains("Prerequisite: ")) {
                            String cleanedText = block.text()
                                    .replace("Prerequisite: ", "");
                            String[] prerequisites = cleanedText.split(" AND ");
                            JSONObject prereqs = new JSONObject();
                            JSONArray andArray = new JSONArray();
                            for (String prereq : prerequisites) {
                                prereq = prereq.replaceAll("or", "OR")
                                        .replaceAll(",", "AND");
                                if (prereq.contains("OR")) {
                                    JSONObject orPrereqs = new JSONObject();
                                    JSONArray orArray = new JSONArray();
                                    String[] subStrings = prereq.split(" OR ");
                                    for (int i = 0; i < subStrings.length; i++) {
                                        subStrings[i] = subStrings[i].replace("(", "")
                                                .replace(")", "");
                                        orArray.add(subStrings[i]);
                                    }
                                    orPrereqs.put("OR", orArray);
                                    andArray.add(orPrereqs);
                                } else {
                                    isAnds = true;
                                    andArray.add(prereq);
                                }
                            }
                            if (isAnds) {
                                prereqs.put("AND", andArray);
                            } else {
                                JSONObject orObj = (JSONObject) andArray.get(0);
                                prereqs.put("OR", orObj.get("OR"));
                            }
                            courseObj.put("prereqs", prereqs);
                        }
                        else {
                            description = description + " " + block.text();
                        }
                    }
                    count++;
                }
                courseObj.put("description", description);
                courseGraph.put(courseCode, courseObj);
            }
        }
        System.out.println(courseGraph);
        return courseGraph;
    }

    public static void createGraph() {
        JSONObject courseGraph = getCourses();
    }

    public static void main(String[] args) {
        getSubject();
        getCourses();
    }

}
