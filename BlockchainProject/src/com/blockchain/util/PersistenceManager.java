package com.blockchain.util;

import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersistenceManager {

    private static final String DEFAULT_FILE = "blockchain_data.json";

    public static void saveChain(List<Block> chain) {
        saveChain(chain, DEFAULT_FILE);
    }

    public static void saveChain(List<Block> chain, String filePath) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            json.append(blockToJson(b));
            if (i < chain.size() - 1) {
                json.append(",\n");
            }
        }
        json.append("\n]");

        try {
            Files.write(Paths.get(filePath), json.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save blockchain: " + e.getMessage());
        }
    }

    public static List<Block> loadChain() {
        return loadChain(DEFAULT_FILE);
    }

    public static List<Block> loadChain(String filePath) {
        List<Block> chain = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return chain; // Empty chain
        }

        try {
            String content = new String(Files.readAllBytes(path));
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1);
            }

            // Split by objects (simple split by "},")
            // This is fragile if data contains "},", but for this controlled env it's
            // likely fine.
            // Better regex: split by }, (?=\{)

            String[] blockJsons = content.split("},");
            for (String blockJson : blockJsons) {
                blockJson = blockJson.trim();
                if (!blockJson.endsWith("}"))
                    blockJson += "}"; // Repair split char
                if (blockJson.length() > 5) { // Minimal length check
                    try {
                        Map<String, Object> map = JsonUtil.parseBlockJson(blockJson);

                        int index = (int) map.get("index");
                        long timestamp = (long) map.get("timestamp");
                        String previousHash = (String) map.get("previousHash");
                        String hash = (String) map.get("hash");
                        String merkleRoot = (String) map.get("merkleRoot");
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> txMaps = (List<Map<String, Object>>) map.get("transactions");
                        List<Transaction> transactions = new ArrayList<>();
                        if (txMaps != null) {
                            for (Map<String, Object> txMap : txMaps) {
                                String sender = (String) txMap.get("sender");
                                String recipient = (String) txMap.get("recipient");
                                double amount = 0.0;
                                Object amtObj = txMap.get("amount");
                                if (amtObj instanceof Double)
                                    amount = (Double) amtObj;
                                else if (amtObj instanceof String)
                                    amount = Double.parseDouble((String) amtObj);

                                long txTimestamp = 0;
                                Object timeObj = txMap.get("timestamp");
                                if (timeObj instanceof Long)
                                    txTimestamp = (Long) timeObj;
                                // Handle potential parsing issues or defaults

                                transactions.add(new Transaction(sender, recipient, amount, txTimestamp));
                            }
                        }

                        Block block = new Block(index, transactions, previousHash, timestamp, hash, merkleRoot);
                        chain.add(block);
                    } catch (Exception e) {
                        System.err.println("Failed to parse block: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to load blockchain: " + e.getMessage());
        }

        return chain;
    }

    private static String blockToJson(Block b) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {");
        sb.append("\"index\":").append(b.getIndex()).append(",");
        sb.append("\"timestamp\":").append(b.getTimestamp()).append(",");
        sb.append("\"previousHash\":\"").append(JsonUtil.escape(b.getPreviousHash())).append("\",");
        sb.append("\"hash\":\"").append(JsonUtil.escape(b.getHash())).append("\",");
        sb.append("\"merkleRoot\":\"").append(JsonUtil.escape(b.getMerkleRoot())).append("\",");

        sb.append("\"transactions\":[");
        List<Transaction> txs = b.getTransactions();
        for (int i = 0; i < txs.size(); i++) {
            Transaction t = txs.get(i);
            sb.append("{");
            sb.append("\"sender\":\"").append(JsonUtil.escape(t.getSender())).append("\",");
            sb.append("\"recipient\":\"").append(JsonUtil.escape(t.getRecipient())).append("\",");
            sb.append("\"amount\":").append(t.getAmount()).append(",");
            sb.append("\"timestamp\":").append(t.getTimestamp());
            sb.append("}");
            if (i < txs.size() - 1)
                sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }
}
