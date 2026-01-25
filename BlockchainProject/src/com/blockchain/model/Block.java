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
    private final String data;
    private final String previousHash;
    private final String hash;

    /**
     * Constructor for a new block.
     * 
     * @param index The position of the block in the chain.
     * @param data The data (transaction) to be stored.
     * @param previousHash The hash of the previous block.
     */
    public Block(int index, String data, String previousHash) {
        this.index = index;
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    /**
     * Constructor for re-creating a block (useful for verification/tampering tests if needed, 
     * though typically we might tamper via setters or a mutable wrapper, checking purely via fields here).
     * Actually for this simple design, we'll keep it simple. To "tamper", we might just need a way 
     * to set a different data but keep the same hash, or vice versa. 
     * Since fields are final, we can't easily tamper *this* object instance. 
     * For the purpose of the assignment, we might want a 'TamperedBlock' subclass or method 
     * that returns a new Block with modified data but old hash to prove validation fails.
     * 
     * See the 'tamperBlock' method idea in the Blockchain class which might replace a node.
     */
    
    // Additional constructor mainly for 'tampering' purposes where we force a specific hash or data state
    public Block(int index, String data, String previousHash, long timestamp, String hash) {
        this.index = index;
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.hash = hash;
    }

    public String calculateHash() {
        return HashUtil.applySha256(
                index +
                Long.toString(timestamp) +
                previousHash +
                data
        );
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Block #" + index + " [Hash: " + hash + ", Prev: " + previousHash + ", Data: " + data + "]";
    }
}
