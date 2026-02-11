package com.blockchain.core;

import com.blockchain.model.Block;
import com.blockchain.model.Transaction;

public class Blockchain {
    private java.util.List<Block> chain;
    private int latestIndex;
    private String latestHash;
    private java.util.function.Consumer<String> logger;

    public Blockchain() {
        this(System.out::println);
    }

    public Blockchain(java.util.function.Consumer<String> logger) {
        this.logger = logger;
        this.chain = com.blockchain.util.PersistenceManager.loadChain();

        if (this.chain.isEmpty()) {
            this.latestIndex = -1;
            this.latestHash = "0"; // Genesis previous hash
        } else {
            Block lastBlock = this.chain.get(this.chain.size() - 1);
            this.latestIndex = lastBlock.getIndex();
            this.latestHash = lastBlock.getHash();
            log("Loaded Blockchain from file. Size: " + this.chain.size());
        }
    }

    private void save() {
        com.blockchain.util.PersistenceManager.saveChain(chain);
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    public void addBlock(String sender, String recipient, double amount) {
        latestIndex++;

        Transaction tx = new Transaction(sender, recipient, amount);
        java.util.List<Transaction> transactions = new java.util.ArrayList<>();
        transactions.add(tx);

        Block newBlock = new Block(latestIndex, transactions, latestHash);

        log("Mining Block " + latestIndex + "...");
        chain.add(newBlock);

        latestHash = newBlock.getHash();
        log("Block " + latestIndex + " added: " + newBlock.getHash());
        log("Merkle Root: " + newBlock.getMerkleRoot());
        save();
    }

    public void displayChain() {
        log("\n=== Blockchain Data ===");
        for (Block block : chain) {
            log(block.toString());
        }
        log("=======================");
    }

    public boolean isChainValid() {
        // Loop through all blocks to check hash integrity
        for (int i = 0; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);

            // 1. Check if the hash is actually correct (re-calculate)
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                log("DATA TAMPERING DETECTED at Block " + currentBlock.getIndex());
                log("Stored Hash: " + currentBlock.getHash());
                log("Calc Hash:   " + currentBlock.calculateHash());
                return false;
            }

            // 2. Check previous hash linkage (except for Genesis block)
            if (i > 0) {
                Block previousBlock = chain.get(i - 1);
                if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                    log("CHAIN BREAKAGE DETECTED at Block " + currentBlock.getIndex());
                    log("Block PrevHash: " + currentBlock.getPreviousHash());
                    log("Actual PrevHash: " + previousBlock.getHash());
                    return false;
                }
            } else {
                // Check genesis previous hash
                if (!currentBlock.getPreviousHash().equals("0")) {
                    log("Genesis Block Invalid Previous Hash");
                    return false;
                }
            }

            // 3. Check Merkle Root Integrity
            // We reconstruct the Merkle Tree from the transactions and see if the root
            // matches
            com.blockchain.ds.MerkleTree tree = new com.blockchain.ds.MerkleTree(currentBlock.getTransactions());
            if (!tree.getRoot().equals(currentBlock.getMerkleRoot())) {
                log("MERKLE ROOT MISMATCH at Block " + currentBlock.getIndex());
                log("Stored Root: " + currentBlock.getMerkleRoot());
                log("Calc Root:   " + tree.getRoot());
                return false;
            }
        }
        return true;
    }

    public void tamperBlock(int index, String sender, String recipient, double amount) {
        for (int i = 0; i < chain.size(); i++) {
            Block b = chain.get(i);
            if (b.getIndex() == index) {
                // Create a tampered block (same hash, different data/merkle root logic implied)
                // Here we simulate tampering by changing the transactions but KEEPING the old
                // hash and merkle root
                // This means when we recalculate hash or merkle root, it won't match.

                java.util.List<Transaction> tamperedTx = new java.util.ArrayList<>();
                tamperedTx.add(new Transaction(sender, recipient, amount));

                // We deliberately keep the OLD hash and OLD Merkle Root to simulate that the
                // header wasn't re-mined properly
                // OR even if we re-mine it (update hash), the Merkle validation against the
                // original might fail if we had an external reference (but here we just check
                // internal consistency)

                // If we want to show Merkle Root failure specifically:
                // We keep the old Merkle Root but change the transactions.

                Block tampered = new Block(
                        b.getIndex(),
                        tamperedTx,
                        b.getPreviousHash(),
                        b.getTimestamp(),
                        b.getHash(),
                        b.getMerkleRoot() // Keeping old root!
                );

                chain.set(i, tampered);
                log("Tampered with Block " + index + ": Transactions replaced.");
                save();
                return;
            }
        }
        log("Block " + index + " not found.");
    }
}
