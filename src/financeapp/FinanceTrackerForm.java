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
    private JLabel naslov;
    private TransactionManager manager;
    private String selectedId = null;


    public FinanceTrackerForm() {
        manager = new TransactionManager();
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row >= 0) {
                selectedId = (String) transactionTable.getValueAt(row, 0); // pretpostavljamo da je ID prva kolona
                typeCombo.setSelectedItem(transactionTable.getValueAt(row, 1));
                amountField.setText(transactionTable.getValueAt(row, 2).toString());
                descriptionField.setText((String) transactionTable.getValueAt(row, 3));
            }
        });
        deleteButton.addActionListener(e -> {
            int row = transactionTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(null, "Morate odabrati transakciju!");
                return;
            }

            // Dijaloški prozor za potvrdu
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Jeste li sigurni da želite izbrisati ovu transakciju?",
                    "Potvrda brisanja",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String id = (String) transactionTable.getValueAt(row, 0); // ID iz prve kolone
                manager.deleteTransaction(id);  // briše zapis iz MongoDB
                loadDataIntoTable();             // osvježi tabelu
                updateSummary();                 // osvježi sažetak
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

                loadDataIntoTable(); // refresh tabele
                updateSummary();     // refresh sažetka

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

        // Dodaj kolone, uključujući ID
        model.addColumn("ID");
        model.addColumn("Vrsta");
        model.addColumn("Iznos");
        model.addColumn("Opis");

        // Popuni tabelu sa podacima iz liste
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getId(),        // ID kolona
                    t.getType(),      // Vrsta
                    t.getAmount(),    // Iznos
                    t.getDescription()// Opis
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
}