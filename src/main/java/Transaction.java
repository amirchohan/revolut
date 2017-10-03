import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
        if (this.amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new Exception("Invalid transaction: Transfer amount must be positive");

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

    public static void createDummyTransactions() {
        List<Account> accounts = DB.getAccounts();

        // deposit money into half of the accounts
        for (int i=0; i < accounts.size(); i+=2 ) {
            try {
                accounts.get(i).deposit(
                        new Transaction(-1, accounts.get(i).getId(), getRandomAmount(99999)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //transfer money between accounts
        for (int i=1; i < accounts.size(); i+=2 ) {
            Account sourceAcc = accounts.get(i-1);
            Account destAcc = accounts.get(i);

            Transaction transaction = new Transaction(sourceAcc.getId(), destAcc.getId(),
                    getRandomAmount(sourceAcc.getBalance().intValue()));
            try {
                transaction.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static BigDecimal getRandomAmount(int max) {
        Random rand = new Random();
        String stringBal = rand.nextInt(max) + "." + rand.nextInt(99);
        return new BigDecimal(stringBal);
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

    static Transaction fromJson(String jsonTrans) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonTrans, Transaction.class);
    }

    static String ListToJson(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\r\n\"transactions\" : [\r\n");
        for (Transaction trans : transactions) {
            sb.append(trans.toJson());
            sb.append(",\r\n");
        }
        sb.replace(sb.length()-3, sb.length(), "");
        sb.append("\r\n]\r\n}");
        return sb.toString();
    }

    static List<Transaction> fromJsonList(String jsonString) throws IOException {
        jsonString = jsonString.substring(20, jsonString.length()-1);

        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonString, Transaction[].class));
    }
}
