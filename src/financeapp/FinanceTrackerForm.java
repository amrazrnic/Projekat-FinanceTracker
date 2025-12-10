

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
    private JLabel naslov;
    private TransactionManager manager;
    public FinanceTrackerForm() {
        manager = new TransactionManager();
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
            model.addRow(new Object[]{
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
