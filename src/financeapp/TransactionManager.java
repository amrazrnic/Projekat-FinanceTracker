package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class TransactionManager {

    private final MongoCollection<Document> collection;

    public TransactionManager() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        collection = db.getCollection("transactions");

        if (collection == null) {
            throw new IllegalStateException("MongoDB collection 'transactions' is null!");
        }
    }

    // Snimi transakciju u bazu
    public void addTransaction(Transaction t) {
        collection.insertOne(t.toDocument());
    }

    // Vrati sve transakcije
    public ArrayList<Transaction> getAllTransactions() {

        ArrayList<Transaction> list = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()) {

            Document d = cursor.next();

            // Vrsta
            String type = d.getString("type");
            if (type == null) type = d.getString("Vrsta");

            // Iznos
            Double amount = d.getDouble("amount");
            if (amount == null) amount = d.getDouble("Iznos");

            // Opis
            String description = d.getString("description");
            if (description == null) description = d.getString("Opis");

            // ID — najvažnije!
            Object idObj = d.get("_id");
            String id = idObj != null ? idObj.toString() : null;

            if (type != null && amount != null && description != null) {
                list.add(new Transaction(id, type, amount, description));
            }
        }
        return list;
    }

        // Ukupni prihodi
    public double getTotalIncome() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if ("Prihod".equals(t.getType()) || "Income".equalsIgnoreCase(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    // Ukupni rashodi
    public double getTotalExpense() {
        double total = 0;
        for (Transaction t : getAllTransactions()) {
            if ("Rashod".equals(t.getType()) || "Expense".equalsIgnoreCase(t.getType())) {
                total += t.getAmount();
            }
        }
        return total;
    }

    // Ažuriranje transakcije po ID-u
    public void updateTransaction(Transaction t) {
        collection.updateOne(
                new Document("_id", new ObjectId(t.getId())),
                new Document("$set",
                        new Document("Vrsta", t.getType())
                                .append("Iznos", t.getAmount())
                                .append("Opis", t.getDescription())
                )
        );
    }

    // Brisanje transakcije po ID-u
    public void deleteTransaction(String id) {
        collection.deleteOne(new Document("_id", new ObjectId(id)));
    }
}
