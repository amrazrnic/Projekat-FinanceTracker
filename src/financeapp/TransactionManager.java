package financeapp;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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

    // Snimi transakciju u bazi podataka
    public void addTransaction(Transaction t) {
        collection.insertOne(t.toDocument());
    }

    // Vrati sve transakcije iz baze
    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> list = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document d = cursor.next();

            // Podrška za stare i nove nazive polja
            String type = d.getString("type");
            if (type == null) type = d.getString("Vrsta");

            Double amount = d.getDouble("amount");
            if (amount == null) amount = d.getDouble("Iznos");

            String description = d.getString("description");
            if (description == null) description = d.getString("Opis");

            // Ako je barem jedno polje null, preskoči zapis
            if (type != null && amount != null && description != null) {
                list.add(new Transaction(type, amount, description));
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
}
