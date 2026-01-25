# How to Run the Blockchain Project

### Prerequisites

Ensure you have Java installed and configured in your system path.

### 1. Open Terminal

Open your command prompt or terminal and navigate to the project directory:

```powershell
cd d:\JAVAPROJECT\BlockchainProject
```

### 2. Compile the Code

Run the following command to compile the Java files and place the class files in a `bin` directory:

```powershell
javac -d bin -sourcepath src src/com/blockchain/main/Main.java
```

### 3. Run the Application

Execute the main class using the class path:

```powershell
java -cp bin com.blockchain.main.Main
```
