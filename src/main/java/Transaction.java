import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Random;

class Transaction {

    private int id;
    private int sourceAccId;
    private int destAccId;
    private BigDecimal amount;
    private boolean successful = false;

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

    public int getId() {
        return id;
    }

    public int getSourceAccId() {
        return sourceAccId;
    }

    public int getDestAccId() {
        return destAccId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean getSuccessful() {
        return successful;
    }

    void execute() throws Exception {
        Account sourceAcc = DB.getAccount(sourceAccId);
        Account destAcc = DB.getAccount(destAccId);
        sourceAcc.withdraw(this);
        destAcc.deposit(this);
        successful = true;
    }

    String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
