package project;

import java.util.*;

public class Document implements Comparable<Document>{
    private HashMap<String, Integer> termFrequency;
    private String courseName;
    private HashMap<String, String> corpus;

    public Document(String courseName, HashMap corpus) {
        this.courseName = courseName;
        termFrequency = new HashMap<String, Integer>();
        this.corpus = corpus;
        readFileAndPreProcess();
    }

    private void readFileAndPreProcess() {
        String[] texts = corpus.get(courseName).split(" ");
        for (String text : texts) {
            String filteredWord = text.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
            if (!(filteredWord.equalsIgnoreCase(""))) {
                if (termFrequency.containsKey(filteredWord)) {
                    int oldCount = termFrequency.get(filteredWord);
                    termFrequency.put(filteredWord, ++oldCount);
                } else {
                    termFrequency.put(filteredWord, 1);
                }
            }
        }
    }

    public double getTermFrequency(String word) {
        if (termFrequency.containsKey(word)) {
            return termFrequency.get(word);
        } else {
            return 0;
        }
    }

    public Set<String> getTermList() {
        return termFrequency.keySet();
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public int compareTo(Document other) {
        return courseName.compareTo(other.getCourseName());
    }

}
