import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

class AccountREST {

    static void accountCreateHandler(HttpServerExchange exchange) {
        Account newAccount = new Account();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(Headers.STATUS, 201);
        exchange.getResponseSender().send(newAccount.toJson());
    }

    static void accountDepositHandler(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(((httpServerExchange, s) -> {

            Transaction depositTransaction = null;
            try {
                depositTransaction = Transaction.fromJson(s);
            } catch (Exception e) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Invalid request: Please send destAccId and amount");
                return;
            }

            if (!DB.checkIfAccountExists(depositTransaction.getDestAccId())) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Error: Account doesn't exist");
                return;
            }
            depositTransaction.setSourceAccId(-1);

            try {
                Account account = DB.getAccount(depositTransaction.getDestAccId());
                account.deposit(depositTransaction);

                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 202);
                httpServerExchange.getResponseSender().send(account.toJson());

            } catch (Exception e) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send(e.getMessage());
            }
        }));
    }
}
