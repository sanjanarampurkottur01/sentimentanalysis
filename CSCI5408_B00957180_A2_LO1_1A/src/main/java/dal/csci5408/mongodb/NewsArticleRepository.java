package dal.csci5408.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dal.csci5408.mongodb.identity.Credentials;
import dal.csci5408.mongodb.model.NewsArticle;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class NewsArticleRepository {

    /**
     * Method to save all the List<@link{NewsArticle}> into the database
     *
     * @param newsArticles - the list of articles to store into the mongo database
     */
    public void save(List<NewsArticle> newsArticles) {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Credentials.MONGO_URL))
                .serverApi(serverApi)
                .build();
        System.out.println("Setting the url and others complete!");
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase newsArticlesDb = mongoClient.getDatabase(Credentials.REUTER_DB);
            MongoCollection<Document> newsCollection = newsArticlesDb.getCollection(
                    Credentials.NEWS_ARTICLE_COLLECTION);

            // Drop the collection if already exists
            if (newsArticlesDb.listCollectionNames().into(
                    new ArrayList<>()).contains(Credentials.NEWS_ARTICLE_COLLECTION)) {
                newsArticlesDb.getCollection(Credentials.NEWS_ARTICLE_COLLECTION).drop();
            }

            // Create the collection
            newsArticlesDb.createCollection(Credentials.NEWS_ARTICLE_COLLECTION);

            System.out.println("Inserting records to the database: " + Credentials.REUTER_DB + " to collection: " +
                    Credentials.NEWS_ARTICLE_COLLECTION);
            for (NewsArticle news : newsArticles) {
                Document newsDocument = new Document()
                        .append("title", news.getTitle())
                        .append("body", news.getBody());
                newsCollection.insertOne(newsDocument);
            }
        }
    }

}
