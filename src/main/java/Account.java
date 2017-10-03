
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Account {

    private int id;
    private BigDecimal balance;
    private List<Transaction> transactions;

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

    void deposit(Transaction transaction) throws Exception {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new Exception("Invalid transaction: Deposit amount must be positive");

        if (transaction.getDestAccId() != this.getId())
            throw new Exception("Invalid transaction: Destination Account ID doesn't match the account ID (500)");

        balance = balance.add(transaction.getAmount());

        if (transaction.getSourceAccId() == -1) transaction.setSuccessful(true);

        transactions.add(0, transaction);
    }

    void withdraw(Transaction transaction) throws Exception {
        if (balance.subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("Insufficient funds");

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new Exception("Invalid transaction: Withdrawal amount must be positive");

        if (transaction.getSourceAccId() != this.getId())
            throw new Exception("Invalid transaction: Source Account ID doesn't match the account ID");

        balance = balance.subtract(transaction.getAmount());

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
        return this.balance;
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
}
