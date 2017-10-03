import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.List;

class TransactionREST {

    static void getTransactionsHandler(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(Headers.STATUS, 200);

        List<Transaction> transactions = DB.getTransactions();
        exchange.getResponseSender().send(Transaction.listToJson(transactions));
    }
}
