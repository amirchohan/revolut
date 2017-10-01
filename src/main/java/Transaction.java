import java.math.BigDecimal;
import java.util.Random;

class Transaction {

    private int id;
    private int sourceAccId;
    private int destAccId;
    private BigDecimal amount;
    private boolean succesful = false;

    Transaction(int _sourceAccount, int _destAccount, BigDecimal _amount) {
        Random rand = new Random();
        do {
            id = rand.nextInt();
        } while (DB.checkIfTransactionExists(id));

        sourceAccId = _sourceAccount;
        destAccId = _destAccount;
        amount = _amount;

        DB.addTransaction(this);
    }

    int getId() {
        return id;
    }

    int getSourceAccId() {
        return sourceAccId;
    }

    int getDestAccId() {
        return destAccId;
    }

    BigDecimal getAmount() {
        return amount;
    }

    boolean checkIfSuccesful() {
        return succesful;
    }

    void execute() throws Exception {
        Account sourceAcc = DB.getAccount(sourceAccId);
        Account destAcc = DB.getAccount(destAccId);
        sourceAcc.withdraw(this);
        destAcc.deposit(this);
        succesful = true;
    }
}
