package com.blockchain.core;

import com.blockchain.ds.AVLTree;
import com.blockchain.model.Block;
import java.util.List;

public class Blockchain {
    private AVLTree blockTree;
    private int latestIndex;
    private String latestHash;
    private java.util.function.Consumer<String> logger;

    public Blockchain() {
        this(System.out::println);
    }

    public Blockchain(java.util.function.Consumer<String> logger) {
        this.blockTree = new AVLTree();
        this.latestIndex = -1;
        this.latestHash = "0"; // Genesis previous hash
        this.logger = logger;
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    public void addBlock(String data) {
        latestIndex++;
        Block newBlock = new Block(latestIndex, data, latestHash);

        log("Mining Block " + latestIndex + "...");
        blockTree.insert(newBlock);

        latestHash = newBlock.getHash();
        log("Block " + latestIndex + " added: " + newBlock.getHash());
    }

    public void displayChain() {
        log("\n=== Blockchain Data (In-Order Traversal) ===");
        blockTree.inOrder(logger);
        log("============================================");
    }

    public boolean isChainValid() {
        List<Block> blocks = blockTree.getAllBlocks();

        // Loop through all blocks to check hash integrity
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);

            // 1. Check if the hash is actually correct (re-calculate)
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                log("DATA TAMPERING DETECTED at Block " + currentBlock.getIndex());
                log("Stored Hash: " + currentBlock.getHash());
                log("Calc Hash:   " + currentBlock.calculateHash());
                return false;
            }

            // 2. Check previous hash linkage (except for Genesis block)
            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
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
        }
        return true;
    }

    public void tamperBlock(int index, String newData) {
        List<Block> blocks = blockTree.getAllBlocks();
        for (Block b : blocks) {
            if (b.getIndex() == index) {
                // Create a tampered block (same hash, different data)
                Block tampered = new Block(b.getIndex(), newData, b.getPreviousHash(), b.getTimestamp(), b.getHash());

                if (blockTree.updateBlock(index, tampered)) {
                    log("Tampered with Block " + index + ": Data changed to '" + newData + "'");
                } else {
                    log("Failed to tamper block " + index);
                }
                return;
            }
        }
        log("Block " + index + " not found.");
    }
}
