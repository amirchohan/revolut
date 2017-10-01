import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

class Transaction {

    private int id;
    private int sourceAccId;
    private int destAccId;
    private BigDecimal amount;
    private boolean successful = false;

    public Transaction() {
        this(0, 0, new BigDecimal("0"));
    }

    Transaction(int _sourceAccount, int _destAccount, BigDecimal _amount) {
        Random rand = new Random();
        do {
            id = rand.nextInt(Integer.MAX_VALUE);
        } while (DB.checkIfTransactionExists(id));

        sourceAccId = _sourceAccount;
        destAccId = _destAccount;
        amount = _amount;

        DB.addTransaction(this);
    }

    void execute() throws Exception {
        Account sourceAcc = DB.getAccount(sourceAccId);
        Account destAcc = DB.getAccount(destAccId);
        sourceAcc.withdraw(this);
        destAcc.deposit(this);
        successful = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int _id) {
        id = _id;
    }

    public int getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(int _sourceAccId) {
        sourceAccId = _sourceAccId;
    }

    public int getDestAccId() {
        return destAccId;
    }

    public void setDestAccId(int _destAccId) {
        destAccId = _destAccId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal _amount) {
        amount = _amount;
    }

    public boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean status) {
        successful = status;
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

    static Transaction fromJson(String jsonTrans) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonTrans, Transaction.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
