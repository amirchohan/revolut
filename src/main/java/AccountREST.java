import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


class AccountREST {

    static void accountCreateHandler(HttpServerExchange exchange) {
        Account newAccount = new Account();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(newAccount.toJson());
    }

    static void accountDepositHandler(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(((httpServerExchange, s) -> {

            Transaction depositTransaction = Transaction.fromJson(s);
            depositTransaction.setSourceAccId(-1);

            Account account = DB.getAccount(depositTransaction.getDestAccId());
            try {
                account.deposit(depositTransaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
            httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            httpServerExchange.getResponseSender().send(account.toJson());
        }));
    }
}
