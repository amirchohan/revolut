import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


class AccountREST {

    static void createAccountHandler(HttpServerExchange exchange) {
        Account newAccount = new Account();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(newAccount.toJson());
    }
}
