package com.blockchain.main;

import com.blockchain.gui.BlockchainGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launch the GUI
        SwingUtilities.invokeLater(() -> BlockchainGUI.createAndShowGUI());
    }
}
