import java.util.Hashtable;

final class DB {

    private static Hashtable<Integer, Account> accounts = new Hashtable<>();
    private static Hashtable<Integer, Transaction> transactions = new Hashtable<>();

    private DB() {
    }


    static boolean checkIfAccountExists(int accountId) {
        return accounts.containsKey(accountId);
    }

    static boolean checkIfTransactionExists(int transactionId) {
        return transactions.containsKey(transactionId);
    }

    static void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }

    static Account getAccount(int accId) {
        return accounts.get(accId);
    }

    static void addTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    static Transaction getTransaction(int transId) {
        return transactions.get(transId);
    }
}
