package dal.csci5408.sentiment;

import dal.csci5408.ReuterProcessor;
import dal.csci5408.sentiment.model.NewsArticle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentimentalAnalyzer {
    private static String reut009 = "src/main/resources/reut2-009.sgm";
    private static String reut014 = "src/main/resources/reut2-014.sgm";

    public static void main(String[] args) {
        ReuterProcessor reuterProcessor = new ReuterProcessor();
        try {
            List<NewsArticle> newsArticles = reuterProcessor.processReuterFile(reut009);
            newsArticles.addAll(reuterProcessor.processReuterFile(reut014));
            List<String> titles = new ArrayList<>();
            for (NewsArticle newsArticle : newsArticles) {
                titles.add(newsArticle.getTitle());
            }
            analyzeSentiment(titles);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void analyzeSentiment(List<String> titles) throws IOException {
        String positiveWordsFilePath = "src/main/resources/positive_words.txt";
        String negativeWordsFilePath = "src/main/resources/negative_words.txt";
        List<String> positiveWords = getWords(positiveWordsFilePath);
        List<String> negativeWords = getWords(negativeWordsFilePath);
        Map<String, Integer> bagOfWords;
        createHeaderInFile();
        int sentenceCount = 0;
        for (String title : titles) {
            sentenceCount++;
            bagOfWords = new HashMap<>();
            for (String word : title.split("\\s+")) {
                if (!bagOfWords.containsKey(word)) {
                    bagOfWords.put(word, 1);
                } else {
                    bagOfWords.put(word, bagOfWords.get(word) + 1);
                }
            }
            calculateAndWriteToFile(title, bagOfWords, positiveWords, negativeWords, sentenceCount);
        }
        System.out.println("Successfully completed the sentimental analysis on the news titles!");
    }

    private static void calculateAndWriteToFile(String title, Map<String, Integer> bagOfWords,
                                                List<String> positiveWords, List<String> negativeWords,
                                                int sentenceCount) throws IOException {
        int score = 0;
        List<String> matchedWords = new ArrayList<>();
        for (Map.Entry<String, Integer> wordCount : bagOfWords.entrySet()) {
            if (positiveWords.contains(wordCount.getKey().toLowerCase())) {
                score = score + wordCount.getValue();
                matchedWords.add(wordCount.getKey());
            } else if (negativeWords.contains(wordCount.getKey().toLowerCase())) {
                score = score - wordCount.getValue();
                matchedWords.add(wordCount.getKey());
            }
        }
        writeToFile(sentenceCount, title, matchedWords, score);
    }

    private static void createHeaderInFile() throws IOException {
        String newsFilePath = "src/main/resources/news";
        File newsFile = new File(newsFilePath);
        newsFile.createNewFile();
        FileWriter newsSentimentWriter = new FileWriter(newsFilePath);
        newsSentimentWriter.write("News#$$Title Content$$Match$$Score$$Polarity\n");
        newsSentimentWriter.close();

    }

    private static void writeToFile(int sentenceCount, String title, List<String> matchedWords, int score)
            throws IOException {
        String newsFilePath = "src/main/resources/news";
        FileWriter newsSentimentWriter = new FileWriter(newsFilePath, true);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sentenceCount);
        stringBuilder.append("$$" + title.toUpperCase());
        stringBuilder.append("$$" + (matchedWords.isEmpty() ? " " : String.join(",", matchedWords)));
        stringBuilder.append("$$" + score);
        stringBuilder.append("$$" + ((score == 0) ? Polarity.Neutral : (score > 0) ?
                Polarity.Positive : Polarity.Negative));
        newsSentimentWriter.write(stringBuilder.toString() + "\n");
        newsSentimentWriter.close();
    }


    private static List<String> getWords(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        List<String> words = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            words.add(line);
        }
        br.close();
        return words;
    }
}
