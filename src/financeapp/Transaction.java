package financeapp;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Transaction {
    private String type;
    private double amount;
    private String description;

    public Transaction(String id, String type, double amount, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }


    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }


    public Document toDocument() {
        return new Document("Vrsta", type)
                .append("Iznos", amount)
                .append("Opis", description);
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getId() { return id; }
    private String id;

}
