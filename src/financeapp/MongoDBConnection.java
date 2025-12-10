package financeapp;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    // MongoDB je lokalno na ovoj adresi
    private static final String URI = "mongodb://localhost:27017";

    // Ovo ce biti naziv nase baze
    private static final String DB_NAME = "financeTrackerDB";

    private static MongoClient client = null;

    public static MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(URI);
        }
        return client.getDatabase(DB_NAME);
    }
}
