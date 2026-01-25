package com.blockchain.gui;

import com.blockchain.core.Blockchain;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class BlockchainGUI extends JFrame {
    private Blockchain blockchain;
    private JTextArea logArea;
    private JTextField dataField;
    private JTextField tamperIndexField;
    private JTextField tamperDataField;

    public BlockchainGUI() {
        setTitle("Blockchain Conceptualization");
        setSize(800, 600);
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
        addPanel.add(new JLabel("Transaction Data:"));
        dataField = new JTextField(40);
        addPanel.add(dataField);
        JButton addButton = new JButton("Add Block");
        addButton.addActionListener(this::onAddBlock);
        addPanel.add(addButton);

        // 2. Actions Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        JButton displayButton = new JButton("Display Chain");
        displayButton.addActionListener(e -> blockchain.displayChain());
        actionPanel.add(displayButton);

        JButton validateButton = new JButton("Validate Blockchain");
        validateButton.addActionListener(this::onValidate);
        actionPanel.add(validateButton);

        // 3. Tamper Panel
        JPanel tamperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tamperPanel.setBorder(BorderFactory.createTitledBorder("Simulate Attack"));
        tamperPanel.add(new JLabel("Block Index:"));
        tamperIndexField = new JTextField(5);
        tamperPanel.add(tamperIndexField);
        tamperPanel.add(new JLabel("New (Fake) Data:"));
        tamperDataField = new JTextField(20);
        tamperPanel.add(tamperDataField);
        JButton tamperButton = new JButton("Tamper Block");
        tamperButton.addActionListener(this::onTamper);
        tamperPanel.add(tamperButton);

        controlsPanel.add(addPanel);
        controlsPanel.add(actionPanel);
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
        String data = dataField.getText().trim();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter transaction data.");
            return;
        }
        blockchain.addBlock(data);
        dataField.setText("");
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
            String data = tamperDataField.getText().trim();
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter fake data.");
                return;
            }
            blockchain.tamperBlock(index, data);
            tamperIndexField.setText("");
            tamperDataField.setText("");
        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(this, "Invalid Index.");
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
