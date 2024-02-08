package dal.csci5408;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to perform the transformation of strings
 */
public class StringOperations {

    private static final String STOP_WORDS_FILEPATH = "/home/sanjanarampurkottur01/stop-words.txt";

    /**
     * Added to avoid unnecessary instantiation
     */
    private StringOperations() {

    }

    /**
     * Method used to clean the input string
     *
     * @param inputText - any string to be cleaned
     * @return
     */
    public static String cleanString(String inputText) {
        return removeStopWords(updateHTMLEncodedValues(removeXMLTags(inputText.trim())));
    }

    /**
     * Method to remove any stop words from the word counter
     *
     * @param inputText
     * @return
     */
    private static String removeStopWords(String inputText) {
        List<String> stopWords = new ArrayList<>();
        try {
            Files.lines(Paths.get(STOP_WORDS_FILEPATH)).forEach(stopWords::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] words = inputText.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!stopWords.contains(word.toLowerCase())) {
                result.append(word).append(" ");
            }
        }
        return result.toString();
    }

    /**
     * Method to decode html encoded values
     *
     * @param inputText
     * @return
     */
    private static String updateHTMLEncodedValues(String inputText) {
        return StringEscapeUtils.unescapeHtml4(inputText);
    }

    /**
     * Method to remove any xml tags present within the input string
     *
     * @param inputText
     * @return
     */
    private static String removeXMLTags(String inputText) {
        return inputText.replaceAll("<[^>]+>", "")
                .replaceAll("\\s+", " ")
                .replaceAll("[\\p{Punct}]", "")
                .replaceAll("\\d", "");
    }
}
