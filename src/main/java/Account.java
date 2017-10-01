
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Account {

    private int id;
    private BigDecimal balance;

    private boolean active;
    private List<Transaction> transactions;

    public Account() {
        this(new BigDecimal(0));
    }

    Account(BigDecimal _balance) {
        Random rand = new Random();
        do {
            id = rand.nextInt();
        } while (DB.checkIfAccountExists(id));

        balance = _balance;
        active = true;
        transactions = new LinkedList<>();

        DB.addAccount(this);
    }

    int getId() {
        return this.id;
    }

    BigDecimal getBalance() {
        return this.balance;
    }

    void deposit(Transaction transaction) throws Exception {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO)<0)
            throw new Exception("Invalid transaction: Deposit amount must be positive");

        if (transaction.getDestAccId() != this.getId())
            throw new Exception("Invalid transaction: Destination Account ID doesn't match the account ID");

        balance = balance.add(transaction.getAmount());
        transactions.add(transaction);
    }

    void withdraw(Transaction transaction) throws Exception {
        if (balance.subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("Insufficient funds");

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new Exception("Invalid transaction: Withdrawal amount must be positive");

        if (transaction.getSourceAccId() != this.getId())
            throw new Exception("Invalid transaction: Source Account ID doesn't match the account ID");

        balance = balance.subtract(transaction.getAmount());
        transactions.add(transaction);
    }

    List<Transaction> getTransactions( ) {
        return transactions;
    }
}
