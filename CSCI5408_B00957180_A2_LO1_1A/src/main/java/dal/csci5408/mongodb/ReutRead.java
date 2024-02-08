package dal.csci5408.mongodb;

import dal.csci5408.ReuterProcessor;
import dal.csci5408.mongodb.identity.Credentials;
import dal.csci5408.mongodb.model.NewsArticle;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.List;

/**
 * The main entry class to read data from the reut sgm files and eventually store the documents into ReuterDB
 * after cleaning
 */
public class ReutRead {
    private static String reut009 = "src/main/resources/reut2-009.sgm";
    private static String reut014 = "src/main/resources/reut2-014.sgm";

    /**
     * The main entry method to start off the read process
     *
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        ReuterProcessor reuterProcessor = new ReuterProcessor();
        try {
            System.out.println("Reading and processing the reut2-009.sgm file");
            List<NewsArticle> newsArticles = reuterProcessor.processReuterFile(reut009);
            System.out.println("Reading and processing the reut2-014.sgm file");
            newsArticles.addAll(reuterProcessor.processReuterFile(reut014));
            // Save the news articles into mongodb
            System.out.println("Save all the news articles to the mongodb collection");
            getNewsArticleRepository().save(newsArticles);
            System.out.println("Successfully inserted " + newsArticles.size() + " " +
                    "news articles to the " +
                    Credentials.REUTER_DB);
        } catch (IOException exception) {
            System.out.println("An error occurred while processing the .sgm file: " + exception);
        }
    }

    private static NewsArticleRepository getNewsArticleRepository() {
        return new NewsArticleRepository();
    }

}
