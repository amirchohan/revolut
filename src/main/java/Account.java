
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account {

    private int id;
    private BigDecimal balance;
    private List<Transaction> transactions;

    private Lock balanceLock = new ReentrantLock();

    public Account() {
        this(new BigDecimal(0));
    }

    Account(BigDecimal _balance) {
        Random rand = new Random();
        do {
            id = rand.nextInt(Integer.MAX_VALUE);
        } while (DB.checkIfAccountExists(id));

        balance = _balance;
        transactions = new LinkedList<>();

        DB.addAccount(this);
    }

    synchronized void deposit(Transaction transaction) throws Exception {
        balanceLock.lock();
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            balanceLock.unlock();
            throw new Exception("Invalid transaction: Deposit amount must be positive");
        }

        if (transaction.getDestAccId() != this.getId()) {
            balanceLock.unlock();
            throw new Exception("Invalid transaction: Destination Account ID doesn't match the account ID (500)");
        }

        balance = balance.add(transaction.getAmount());
        balanceLock.unlock();

        if (transaction.getSourceAccId() == -1) transaction.setSuccessful(true);

        transactions.add(0, transaction);
    }

    synchronized void withdraw(Transaction transaction) throws Exception {
        balanceLock.lock();
        if (balance.subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            balanceLock.unlock();
            throw new Exception("Insufficient funds");
        }

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            balanceLock.unlock();
            throw new Exception("Invalid transaction: Withdrawal amount must be positive");
        }

        if (transaction.getSourceAccId() != this.getId()) {
            balanceLock.unlock();
            throw new Exception("Invalid transaction: Source Account ID doesn't match the account ID");
        }

        balance = balance.subtract(transaction.getAmount());
        balanceLock.unlock();

        if (transaction.getDestAccId() == -1) transaction.setSuccessful(true);

        transactions.add(0, transaction);
    }

    static void createDemoAccounts(int num) {
        for (int i=0; i < num; i++) {
            new Account(Transaction.getRandomAmount(99999));
        }
    }


    public int getId() {
        return this.id;
    }

    public void setId(int _id) {
        id = _id;
    }

    public BigDecimal getBalance() {
        BigDecimal bal;
        balanceLock.lock();
        bal = this.balance;
        balanceLock.unlock();
        return bal;
    }

    public void setBalance(BigDecimal _balance) {
        balance = _balance;
    }

    public List<Transaction> getTransactions( ) {
        return transactions;
    }

    public void setTransactions(List<Transaction> _transactions) {
        transactions = _transactions;
    }

    String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    static Account fromJson(String jsonAcc) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonAcc, Account.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (getClass() != o.getClass())
            return false;

        Account otherAcc = (Account) o;

        return (this.getId() == otherAcc.getId()) &&
                (this.getBalance().compareTo(otherAcc.getBalance()) == 0);
    }

    static List<Account> fromJsonList(String jsonString) throws IOException {
        jsonString = jsonString.substring(16, jsonString.length()-1);

        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonString, Account[].class));
    }

    public static String listToJson(List<Account> accounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\r\n\"accounts\" : [\r\n");
        for (Account acc : accounts) {
            sb.append(acc.toJson());
            sb.append(",\r\n");
        }
        sb.replace(sb.length()-3, sb.length(), "");
        sb.append("\r\n]\r\n}");
        return sb.toString();
    }

    public void refundTransaction(Transaction transaction) {
        balanceLock.lock();
        balance = balance.add(transaction.getAmount());
        balanceLock.unlock();

        transactions.remove(transaction);
    }
}
