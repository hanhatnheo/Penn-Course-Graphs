package project;

import org.json.JSONObject;
import java.util.*;

public class Corpus {
    private static HashMap<String, Set<Document>> invertedIndex;
    private static ArrayList<Document> documents;
    public static void createCorpus() {
        JSONObject courseGraph = Scraper.getCourses();
        documents = new ArrayList<>();
        String[] elementNames = JSONObject.getNames(courseGraph);
        HashMap<String, String> corpus = new HashMap<>();
        for (String element : elementNames) {
            JSONObject values = (JSONObject) courseGraph.get(element);
            String[] valueKeys = JSONObject.getNames(values);
            for (String valueKey : valueKeys) {
                if (valueKey.equals("description")) {
                    String value = values.get(valueKey).toString();
                    corpus.put(element, value);
                }
            }
        }
        Set<String> keySets = corpus.keySet();
        for (String key : keySets) {
            Document d = new Document(key, corpus);
            documents.add(d);
        }
        invertedIndex = new HashMap<>();
        for (Document document : documents) {
            Set<String> terms = document.getTermList();

            for (String term : terms) {
                if (invertedIndex.containsKey(term)) {
                    Set<Document> list = invertedIndex.get(term);
                    list.add(document);
                } else {
                    Set<Document> list = new TreeSet<Document>();
                    list.add(document);
                    invertedIndex.put(term, list);
                }
            }
        }
    }

    public static double getInverseDocumentFrequency(String term) {
        if (invertedIndex.containsKey(term)) {
            double size = documents.size();
            Set<Document> list = invertedIndex.get(term);
            double documentFrequency = list.size();

            return Math.log10(size / documentFrequency);
        } else {
            return 0;
        }
    }

    public static ArrayList<Document> getDocuments() {
        return documents;
    }
    public static HashMap<String, Set<Document>> getInvertedIndex() {
        return invertedIndex;
    }
}
