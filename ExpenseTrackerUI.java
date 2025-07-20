import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpenseTrackerUI extends JFrame {

    private final ExpenseManager manager = new ExpenseManager();

    private final DefaultListModel<Person> personListModel = new DefaultListModel<>();
    private final DefaultListModel<Bill> billListModel = new DefaultListModel<>();
    private final DefaultTableModel summaryTableModel = new DefaultTableModel(new Object[]{"Person", "Balance"}, 0);

    public ExpenseTrackerUI() {
        super("Bill Splitter & Expense Tracker");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Add Person", createAddPersonPanel());
        tabs.addTab("Add Bill", createAddBillPanel());
        tabs.addTab("Summary", createSummaryPanel());
        tabs.addTab("View Bills", createViewBillsPanel());

        add(tabs);
    }

    private JPanel createAddPersonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Enter Person Name:");
        label.setFont(new Font("Arial", Font.BOLD, 16));

        JTextField nameField = new JTextField();
        JButton addBtn = new JButton("Add Person");

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if(name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!manager.addPerson(name)) {
                JOptionPane.showMessageDialog(this, "Person already exists or invalid", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Person p = manager.getPersonByName(name);
            personListModel.addElement(p);
            nameField.setText("");
            updateSummary();
            JOptionPane.showMessageDialog(this, "Person added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(nameField, BorderLayout.CENTER);
        panel.add(addBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
    
        JTextField descField = new JTextField();
    
        // Create separate ComboBoxModel for payer
        // Create ComboBoxModel for payer
DefaultComboBoxModel<Person> payerComboModel = new DefaultComboBoxModel<>();
JComboBox<Person> payerCombo = new JComboBox<>(payerComboModel);

// Sync payerComboModel with personListModel
personListModel.addListDataListener(new ListDataListener() {
    private void syncModels() {
        payerComboModel.removeAllElements();
        for (int i = 0; i < personListModel.size(); i++) {
            payerComboModel.addElement(personListModel.getElementAt(i));
        }
        if (payerComboModel.getSize() > 0) {
            payerCombo.setSelectedIndex(0);
        }
    }
    @Override
    public void contentsChanged(ListDataEvent e) { syncModels(); }
    @Override
    public void intervalAdded(ListDataEvent e) { syncModels(); }
    @Override
    public void intervalRemoved(ListDataEvent e) { syncModels(); }
});

// Initial fill for payerComboModel
for (int i = 0; i < personListModel.size(); i++) {
    payerComboModel.addElement(personListModel.getElementAt(i));
}
if (payerComboModel.getSize() > 0) {
    payerCombo.setSelectedIndex(0);
}

    
        JTextField amountField = new JTextField();
    
        JLabel participantsLabel = new JLabel("Select Participants (Ctrl+Click to select multiple):");
        DefaultListModel<Person> participantListModel = new DefaultListModel<>();
        JList<Person> participantList = new JList<>(participantListModel);
        participantList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane participantScroll = new JScrollPane(participantList);
    
        // Sync participants with persons (copy from personListModel)
        personListModel.addListDataListener(new ListDataListener() {
            private void updateParticipants() {
                participantListModel.clear();
                payerComboModel.removeAllElements();
                for(int i=0; i<personListModel.size(); i++) {
                    Person p = personListModel.getElementAt(i);
                    participantListModel.addElement(p);
                    payerComboModel.addElement(p);
                }
                // Optional: if payerComboModel now has elements, set first one selected
                if(payerComboModel.getSize() > 0) {
                    payerCombo.setSelectedIndex(0);
                }
            }
            public void contentsChanged(ListDataEvent e) { updateParticipants(); }
            public void intervalAdded(ListDataEvent e) { updateParticipants(); }
            public void intervalRemoved(ListDataEvent e) { updateParticipants(); }
        });
    
        // Initialize participants and payer combo box models at startup
        for(int i=0; i<personListModel.size(); i++) {
            Person p = personListModel.getElementAt(i);
            participantListModel.addElement(p);
            payerComboModel.addElement(p);
        }
        if(payerComboModel.getSize() > 0) {
            payerCombo.setSelectedIndex(0);
        }
    
        form.add(new JLabel("Bill Description:"));
        form.add(descField);
        form.add(new JLabel("Payer:"));
        form.add(payerCombo);
        form.add(new JLabel("Amount:"));
        form.add(amountField);
        form.add(participantsLabel);
        form.add(participantScroll);
    
        JButton addBillBtn = new JButton("Add Bill");
        addBillBtn.addActionListener(e -> {
            String desc = descField.getText().trim();
            Person payer = (Person) payerCombo.getSelectedItem();
            String amountText = amountField.getText().trim();
            List<Person> selectedParticipants = participantList.getSelectedValuesList();
    
            if(desc.isEmpty() || payer == null || amountText.isEmpty() || selectedParticipants.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields and select participants", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if(amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            boolean success = manager.addBill(desc, payer, amount, selectedParticipants);
            if(!success) {
                JOptionPane.showMessageDialog(this, "Failed to add bill", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            billListModel.addElement(manager.getAllBills().get(manager.getAllBills().size()-1));
    
            descField.setText("");
            amountField.setText("");
            participantList.clearSelection();
    
            updateSummary();
            JOptionPane.showMessageDialog(this, "Bill added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    
        panel.add(form, BorderLayout.CENTER);
        panel.add(addBillBtn, BorderLayout.SOUTH);
    
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTable summaryTable = new JTable(summaryTableModel);
        JScrollPane scrollPane = new JScrollPane(summaryTable);

        summaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summaryTable.setRowHeight(25);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createViewBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JList<Bill> billJList = new JList<>(billListModel);
        billJList.setFont(new Font("Tahoma", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(billJList);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateSummary() {
        summaryTableModel.setRowCount(0);
        for(Person p : manager.getAllPersons()) {
            summaryTableModel.addRow(new Object[]{p.getName(), String.format("%.2f", p.getBalance())});
        }
    }
}
