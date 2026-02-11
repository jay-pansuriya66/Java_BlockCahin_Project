package com.blockchain.model;

import com.blockchain.util.HashUtil;
import java.util.Date;

public class Transaction {
    private final String sender;
    private final String recipient;
    private final double amount;
    private final long timestamp;

    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = new Date().getTime();
    }

    // Constructor for loading from persistence or tampering
    public Transaction(String sender, String recipient, double amount, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return sender + ":" + recipient + ":" + amount + ":" + timestamp;
    }

    public String calculateHash() {
        return HashUtil.applySha256(toString());
    }
}
