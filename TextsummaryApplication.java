package com.example.textsummarized.textsummary;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TextsummaryApplication{

    // Calculated Term Frequency (TF) for a given term in a document
    public static double calculateTF(List<String> document, String term) {
        long termCount = document.stream().filter(word -> word.equalsIgnoreCase(term)).count();
        return (double) termCount / document.size();
    }

    // Calculated Inverse Document Frequency (IDF) for a given term across all documents
    public static double calculateIDF(List<List<String>> allDocuments, String term) {
        long docsWithTerm = allDocuments.stream().filter(doc -> doc.contains(term)).count();
        return Math.log((double) allDocuments.size() / (docsWithTerm + 1));
    }

    // Calculated TF-IDF score for a term in a document
    public static double calculateTFIDF(List<String> document, List<List<String>> allDocuments, String term) {
        double tf = calculateTF(document, term);
        double idf = calculateIDF(allDocuments, term);
        return tf * idf;
    }

    // Summarize the document using TF-IDF
    public static String summarize(List<String> document, int numSentences) {
        // Create a set of all unique words in the document
        Set<String> uniqueWords = document.stream()
                                          .map(sentence -> sentence.split("\\s+"))
                                          .flatMap(Arrays::stream)
                                          .map(String::toLowerCase)
                                          .collect(Collectors.toSet());

        // Calculate TF-IDF score for each word
        Map<String, Double> tfidfScores = uniqueWords.stream()
                                                     .collect(Collectors.toMap(
                                                         word -> word,
                                                         word -> calculateTFIDF(document, Collections.singletonList(document), word)
                                                     ));

        // Calculate sentence scores based on TF-IDF scores of words
        Map<String, Double> sentenceScores = new HashMap<>();
        for (String sentence : document) {
            double score = Arrays.stream(sentence.split("\\s+"))
                                 .map(String::toLowerCase)
                                 .mapToDouble(word -> tfidfScores.getOrDefault(word, 0.0))
                                 .sum();
            sentenceScores.put(sentence, score);
        }

        // Sort sentences by score in descending order
        List<String> sortedSentences = sentenceScores.entrySet()
                                                     .stream()
                                                     .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                                                     .map(Map.Entry::getKey)
                                                     .collect(Collectors.toList());

        // Select top N sentences
        List<String> topSentences = sortedSentences.stream().limit(numSentences).collect(Collectors.toList());

        return String.join(". ", topSentences) + ".";
    }
    

    public static void main(String[] args) {
        SpringApplication.run(TextsummaryApplication.class, args);
    }
}
