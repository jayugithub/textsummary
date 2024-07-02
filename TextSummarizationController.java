package com.example.textsummarized.textsummary;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
public class TextSummarizationController {

    @PostMapping("/summarize")
    public String summarizeText(@RequestBody String text) {
        int numSentences = 5; // Adjust as needed
        List<String> document = Arrays.asList(text.split("\\.\\s*"));
        return TextsummaryApplication.summarize(document, numSentences);
    }
}
