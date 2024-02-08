package dal.csci5408;

import dal.csci5408.sentiment.model.NewsArticle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReuterProcessor {
    /**
     * A private method to read the reuter data from the .sgm files
     *
     * @param reuterTextList - the final string containing the reuterText between the <REUTER></REUTER>
     * @param reuterPattern  - the pattern to match the content between <REUTER></REUTER>
     * @param file           - the relative path of the file to be read from
     * @throws IOException - if unable to read from the file path
     */
    private static void readReuterFile(List<String> reuterTextList, String reuterPattern, String file)
            throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder data = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            data.append(line);
        }
        br.close();

        Matcher reuterMatcher = Pattern.compile(reuterPattern, Pattern.DOTALL).matcher(data);
        while (reuterMatcher.find()) {
            String reuter = reuterMatcher.group(1);
            reuterTextList.add(reuter);
        }
    }

    /**
     * Cleaning Method to remove html content typecodes and any other special characters from the string
     *
     * @param data
     * @return
     */
    private static String getCleanedData(String data) {
        data = data.replaceAll("\r\n", " ").trim().replaceAll(" +", " ");
        data = data.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&le;", "<=").replaceAll("&ge;", ">=")
                .replaceAll("&apos;", "'").replaceAll("&quot;", "\"")
                .replaceAll("&amp;", "&").replaceAll("%20", " ")
                .replaceAll("%21", "!").replaceAll("%22", "\"")
                .replaceAll("%26", "&").replaceAll("%3C", "<")
                .replaceAll("%3E", ">");
        return data;
    }

    /**
     * Processes the reuter file and creates List<@link{NewsArticle}>
     *
     * @param filePath containing the content
     * @return the List of NewsArticle
     * @throws IOException
     */
    public List<NewsArticle> processReuterFile(String filePath) throws IOException {
        List<String> reuterTextList = new ArrayList<>();
        String reuterPattern = "<REUTERS.+?>(.*?)</REUTERS>";
        String titlePattern = "<TITLE.*?>(.*?)</TITLE>";
        String bodyPattern = "<BODY>(.*?)</BODY>";
        List<NewsArticle> newsArticles = new ArrayList<>();
        try {
            System.out.println("Reading and processing the " + filePath + " file");
            readReuterFile(reuterTextList, reuterPattern, filePath);
            System.out.println("Add the content present in reuterTextList between the <TITLE></TITLE> and <BODY></BODY> " +
                    "into newsArticles");
            for (String reuter : reuterTextList) {
                Matcher titleMatcher = Pattern.compile(titlePattern, Pattern.DOTALL).matcher(reuter);
                String titleText = titleMatcher.find() ? titleMatcher.group(1) : "";

                Matcher bodyMatcher = Pattern.compile(bodyPattern, Pattern.DOTALL).matcher(reuter);
                String bodyText = bodyMatcher.find() ? bodyMatcher.group(1) : "";

                titleText = getCleanedData(titleText);
                bodyText = getCleanedData(bodyText);

                if (!(titleText.trim().isEmpty() && bodyText.trim().isEmpty())) {
                    newsArticles.add(new NewsArticle(titleText, bodyText));
                }
            }
        } catch (IOException exception) {
            throw exception;
        }
        return newsArticles;
    }
}
