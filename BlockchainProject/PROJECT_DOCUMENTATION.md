# Blockchain Project Documentation

## 1. Project Overview

This project is a Java-based simulation of a Blockchain system. It demonstrates the core principles of blockchain technology, including **cryptographic hashing**, **linked list structure**, **Merkle Tree data integrity**, and **persistence**.

## 2. Key Concepts

### 2.1 The Block

Each block in the chain contains the following data:

- **Index**: The position of the block (0, 1, 2, ...).
- **Timestamp**: When the block was created.
- **Transactions**: A list of data/transactions stored in the block.
- **Previous Hash**: The hash of the preceding block, linking them together.
- **Merkle Root**: A single hash representing all transactions in the block.
- **Hash**: The unique digital fingerprint of the block itself, calculated from all the above fields.

### 2.2 Merkle Tree

Instead of hashing all transactions into a single string, this project uses a **Merkle Tree**.

- Every transaction is hashed.
- These hashes are paired and hashed again, repeatedly, until a single hash remains: the **Merkle Root**.
- **Benefit**: This allows for efficient verification of data integrity. If a single transaction is altered, the Merkle Root changes completely, invalidating the block.

### 2.3 Persistence (JSON)

The blockchain state is saved to a file named `blockchain_data.json`.

- When you add a block or restart the application, the system automatically saves/loads the chain.
- This ensures that your blockchain data is not lost when you close the program.

## 3. How It Works

### adding a Block

When you enter transactions (e.g., "A pays B 100") and click "Add Block":

1.  The transactions are stored in a new `Block` object.
2.  A **Merkle Root** is calculated from these transactions.
3.  The block's **Hash** is calculated using: `Index + Timestamp + PreviousHash + MerkleRoot`.
4.  The block is added to the chain and saved to `blockchain_data.json`.

### Validating the Chain

The system validates the blockchain by checking three conditions for every block:

1.  **stored Hash == Calculated Hash**: Ensures the block header hasn't been tampered with.
2.  **Previous Hash == Previous Block's Hash**: Ensures the chain link is unbroken.
3.  **Stored Merkle Root == Calculated Merkle Root**: Ensures the transactions inside the block haven't been altered.

### Tampering (Simulation)

The "Simulate Attack" feature allows you to modify the transactions of an existing block _without_ updating its Merkle Root or Hash.

- This simulates a malicious actor trying to change history.
- When you run "Validate Blockchain" after tampering, the system detects a **Merkle Root Mismatch** and marks the chain as **INVALID**.

## 4. Project Structure

- **`com.blockchain.core`**: Contains the `Blockchain` class which manages the chain list and validation logic.
- **`com.blockchain.model`**: Contains the `Block` class defining the data structure.
- **`com.blockchain.ds`**: Contains `MerkleTree.java` for cryptographic tree logic.
- **`com.blockchain.gui`**: Contains the Swing-based User Interface.
- **`com.blockchain.util`**: Contains utility classes:
  - `HashUtil`: For SHA-256 hashing.
  - `JsonUtil`: For parsing/generating JSON.
  - `PersistenceManager`: For file I/O operations.
- **`com.blockchain.main`**: Contains the `Main` entry point.

## 5. How to Run

1.  **Prerequisites**: Java Development Kit (JDK) installed.
2.  **Compile & Run**:
    Run the `Main` class from your IDE or use the command line:
    ```bash
    javac -d bin -sourcepath src src/com/blockchain/main/Main.java
    java -cp bin com.blockchain.main.Main
    ```

## 6. Usage Guide

1.  **Launch the Application**: You will see the "Blockchain Conceptualization" window.
2.  **Add Data**: In the "Transaction Data" field, type your data (e.g., `Tx1, Tx2`). Click **Add Block**.
3.  **View Chain**: The log area will show the new block's hash and Merkle Root.
4.  **Validate**: Click **Validate Blockchain** to confirm everything is secure.
5.  **Test Security**:
    - Go to "Simulate Attack".
    - Enter a Block Index (e.g., `0`) and new fake data (e.g., `HackedTx`).
    - Click **Tamper Block**.
    - Click **Validate Blockchain** again. You should see an error message indicating invalidity.
6.  **Persistence**: Close the app and reopen it. Your blocks will be reloaded automatically.
