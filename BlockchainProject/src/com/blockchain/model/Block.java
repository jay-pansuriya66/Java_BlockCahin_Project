package com.blockchain.model;

import com.blockchain.util.HashUtil;
import java.util.Date;

/**
 * Represents a block in the blockchain.
 * This class is immutable.
 */
public class Block {
    private final int index;
    private final long timestamp;
    private final java.util.List<String> transactions;
    private final String merkleRoot;
    private final String previousHash;
    private final String hash;

    /**
     * Constructor for a new block.
     * 
     * @param index        The position of the block in the chain.
     * @param transactions The list of transactions to be stored.
     * @param previousHash The hash of the previous block.
     */
    public Block(int index, java.util.List<String> transactions, String previousHash) {
        this.index = index;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.merkleRoot = calculateMerkleRoot();
        this.hash = calculateHash();
    }

    // Additional constructor mainly for 'tampering' purposes where we force a
    // specific hash or data state
    public Block(int index, java.util.List<String> transactions, String previousHash, long timestamp, String hash,
            String merkleRoot) {
        this.index = index;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.hash = hash;
        this.merkleRoot = merkleRoot;
    }

    public String calculateHash() {
        return HashUtil.applySha256(
                index +
                        Long.toString(timestamp) +
                        previousHash +
                        merkleRoot);
    }

    private String calculateMerkleRoot() {
        com.blockchain.ds.MerkleTree tree = new com.blockchain.ds.MerkleTree(transactions);
        return tree.getRoot();
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public java.util.List<String> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    @Override
    public String toString() {
        return "Block #" + index + " [Hash: " + hash + ", Prev: " + previousHash + ", MerkleRoot: " + merkleRoot
                + ", TxCount: " + (transactions != null ? transactions.size() : 0) + "]";
    }
}
