package com.blockchain.gui;

import com.blockchain.core.Blockchain;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class BlockchainGUI extends JFrame {
    private Blockchain blockchain;
    private JTextArea logArea;

    // Add Block Fields
    private JTextField senderField;
    private JTextField recipientField;
    private JTextField amountField;

    // Tamper Fields
    private JTextField tamperIndexField;
    private JTextField tamperSenderField;
    private JTextField tamperRecipientField;
    private JTextField tamperAmountField;

    public BlockchainGUI() {
        setTitle("Blockchain Conceptualization");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Blockchain with a logger that appends to our text area
        blockchain = new Blockchain(this::log);

        // Layout
        setLayout(new BorderLayout());

        // --- Output Log Area ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        // Auto-scroll
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Log & Blockchain Output"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Controls Panel ---
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(3, 1));

        // 1. Add Block Panel
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Block"));

        addPanel.add(new JLabel("Sender:"));
        senderField = new JTextField(10);
        addPanel.add(senderField);

        addPanel.add(new JLabel("Recipient:"));
        recipientField = new JTextField(10);
        addPanel.add(recipientField);

        addPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(8);
        addPanel.add(amountField);

        JButton addButton = new JButton("Add Block");
        addButton.addActionListener(this::onAddBlock);
        addPanel.add(addButton);

        controlsPanel.add(addPanel);

        // 2. Actions Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        JButton displayButton = new JButton("Display Chain");
        displayButton.addActionListener(e -> blockchain.displayChain());
        actionPanel.add(displayButton);

        JButton validateButton = new JButton("Validate Blockchain");
        validateButton.addActionListener(this::onValidate);
        actionPanel.add(validateButton);

        controlsPanel.add(actionPanel);

        // 3. Tamper Panel
        JPanel tamperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tamperPanel.setBorder(BorderFactory.createTitledBorder("Simulate Attack"));

        tamperPanel.add(new JLabel("Index:"));
        tamperIndexField = new JTextField(3);
        tamperPanel.add(tamperIndexField);

        tamperPanel.add(new JLabel("Fake Sender:"));
        tamperSenderField = new JTextField(8);
        tamperPanel.add(tamperSenderField);

        tamperPanel.add(new JLabel("Fake Recip:"));
        tamperRecipientField = new JTextField(8);
        tamperPanel.add(tamperRecipientField);

        tamperPanel.add(new JLabel("Fake Amt:"));
        tamperAmountField = new JTextField(5);
        tamperPanel.add(tamperAmountField);

        JButton tamperButton = new JButton("Tamper Block");
        tamperButton.addActionListener(this::onTamper);
        tamperPanel.add(tamperButton);

        controlsPanel.add(tamperPanel);

        add(controlsPanel, BorderLayout.SOUTH);

        log("System Ready. Add a block to start.");
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
        });
    }

    private void onAddBlock(ActionEvent e) {
        String sender = senderField.getText().trim();
        String recipient = recipientField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (sender.isEmpty() || recipient.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all transaction details.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            blockchain.addBlock(sender, recipient, amount);

            senderField.setText("");
            recipientField.setText("");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Amount. Please enter a number.");
        }
    }

    private void onValidate(ActionEvent e) {
        boolean valid = blockchain.isChainValid();
        String result = valid ? "VALID" : "INVALID";
        log("\nBlockchain Integrity Check: " + result);
        if (!valid) {
            JOptionPane.showMessageDialog(this, "Blockchain Validation Failed! Check logs.", "Security Alert",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Blockchain is Valid.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onTamper(ActionEvent e) {
        try {
            int index = Integer.parseInt(tamperIndexField.getText().trim());
            String sender = tamperSenderField.getText().trim();
            String recipient = tamperRecipientField.getText().trim();
            String amountStr = tamperAmountField.getText().trim();

            if (sender.isEmpty() || recipient.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all fake transaction details.");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            blockchain.tamperBlock(index, sender, recipient, amount);

            tamperIndexField.setText("");
            tamperSenderField.setText("");
            tamperRecipientField.setText("");
            tamperAmountField.setText("");

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Invalid Index or Amount.");
        }
    }

    // Helper for starting
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        new BlockchainGUI().setVisible(true);
    }
}
