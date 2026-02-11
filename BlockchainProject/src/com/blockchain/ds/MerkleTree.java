package com.blockchain.ds;

import com.blockchain.util.HashUtil;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    private String root;

    public MerkleTree(List<com.blockchain.model.Transaction> transactions) {
        this.root = calculateMerkleRoot(transactions);
    }

    public String getRoot() {
        return root;
    }

    private String calculateMerkleRoot(List<com.blockchain.model.Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }

        List<com.blockchain.model.Transaction> tempTxList = new ArrayList<>(transactions);

        // Convert all transactions to their hashes
        List<String> hashes = new ArrayList<>();
        for (com.blockchain.model.Transaction tx : tempTxList) {
            hashes.add(tx.calculateHash());
        }

        // Recursively find the Merkle Root
        return findMerkleRoot(hashes);
    }

    private String findMerkleRoot(List<String> hashes) {
        if (hashes.size() == 1) {
            return hashes.get(0);
        }

        List<String> newHashes = new ArrayList<>();

        // Process pairs
        for (int i = 0; i < hashes.size(); i += 2) {
            String left = hashes.get(i);
            String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left; // Duplicate last if odd

            String combined = left + right;
            String computedHash = HashUtil.applySha256(combined);
            newHashes.add(computedHash);
        }

        return findMerkleRoot(newHashes);
    }
}
