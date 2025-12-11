package financeapp;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
public class FinanceTrackerForm {
    private JPanel mainPanel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeCombo;
    private JButton addButton;
    private JTable transactionTable;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;
    private JLabel text1;
    private JLabel text2;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JLabel naslov;
    private TransactionManager manager;
    private String selectedId = null;


    public FinanceTrackerForm() {
        manager = new TransactionManager();
        typeCombo.removeAllItems();
        typeCombo.addItem("Prihod");
        typeCombo.addItem("Rashod");
        typeCombo.addItem("Hrana");
        typeCombo.addItem("Racuni");
        typeCombo.addItem("Zabava");
        typeCombo.addItem("Prijevoz");
        typeCombo.addItem("Ostalo");

        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = transactionTable.getSelectedRow();
                if (row >= 0) {

                    ArrayList<Transaction> list = manager.getAllTransactions();
                    Transaction t = list.get(row);

                    selectedId = t.getId();

                    typeCombo.setSelectedItem(t.getType());
                    amountField.setText(String.valueOf(t.getAmount()));
                    descriptionField.setText(t.getDescription());
                }
            }
        });

        exportButton.addActionListener(e -> exportData());
        deleteButton.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(null, "Morate odabrati transakciju!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Jeste li sigurni da želite izbrisati ovu transakciju?",
                    "Potvrda brisanja",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {

                ArrayList<Transaction> list = manager.getAllTransactions();
                Transaction selected = list.get(row);

                manager.deleteTransaction(selected.getId());

                loadDataIntoTable();
                updateSummary();

                JOptionPane.showMessageDialog(null, "Transakcija izbrisana!");
            }
        });


        updateButton.addActionListener(e -> {
            if (selectedId == null) {
                JOptionPane.showMessageDialog(null, "Morate odabrati transakciju!");
                return;
            }
            try {
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();

                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Opis ne može biti prazan!");
                    return;
                }

                Transaction updated = new Transaction(selectedId, type, amount, description);
                manager.updateTransaction(updated);

                loadDataIntoTable();
                updateSummary();

                amountField.setText("");
                descriptionField.setText("");
                selectedId = null;

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Iznos mora biti broj!");
            }
        });

        loadDataIntoTable();
        updateSummary();
        addButton.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Opis ne može biti prazan!");
                    return;
                }
                Transaction t = new Transaction(type, amount, description);
                manager.addTransaction(t);
                loadDataIntoTable();
                updateSummary();
                amountField.setText("");
                descriptionField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Iznos mora biti broj!");
            }
        });
    }

    private void loadDataIntoTable() {
        ArrayList<Transaction> list = manager.getAllTransactions();
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        for (Transaction t : list) {
            model.addRow(new Object[] {
                    t.getType(),
                    t.getAmount(),
                    t.getDescription()
            });
        }

        transactionTable.setModel(model);
    }


    private void updateSummary() {
        double income = manager.getTotalIncome();
        double expense = manager.getTotalExpense();
        double balance = income - expense;
        incomeLabel.setText("Prihod: " + income);
        expenseLabel.setText("Rashod: " + expense);
        balanceLabel.setText("Saldo: " + balance);
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }
    private void exportData() {
        try {
            String filePath = System.getProperty("user.home") + "/Desktop/finansije_export.txt";

            double income = manager.getTotalIncome();
            double expense = manager.getTotalExpense();
            double balance = income - expense;

            ArrayList<Transaction> list = manager.getAllTransactions();
            java.util.HashMap<String, Double> categoryTotals = new java.util.HashMap<>();

            for (Transaction t : list) {
                String type = t.getType();
                double amount = t.getAmount();

                if (!type.equals("Prihod")) {
                    categoryTotals.put(type, categoryTotals.getOrDefault(type, 0.0) + amount);
                }
            }

            java.io.PrintWriter writer = new java.io.PrintWriter(filePath);

            writer.println("Ukupni prihod: " + income);
            writer.println("Ukupni rashod: " + expense);
            writer.println("Stanje: " + balance);
            writer.println();
            writer.println("Rashodi po kategorijama:");

            for (String category : categoryTotals.keySet()) {
                writer.println(category + ": " + categoryTotals.get(category));
            }

            writer.close();

            JOptionPane.showMessageDialog(null,
                    "Podaci uspješno eksportovani!\nLokacija: Desktop -> finansije_export.txt");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Greška prilikom eksportovanja: " + ex.getMessage());
        }
    }

}