import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;

final class RestServer {

    private static int PORT = 8080;
    private static String HOSTNAME = "localhost";
    private static Undertow server;

    private static final HttpHandler ROUTES = new RoutingHandler()
            .get("/", RestServer::defaultHandler)
            .get("/account/{accId}", AccountREST::getAccountHandler)
            .get("/account/create", AccountREST::accountCreateHandler)
            .get("/accounts", AccountREST::getAccountsHandler)
            .post("/account/deposit", AccountREST::accountDepositHandler)
            .post("/account/withdraw", AccountREST::accountWithdrawalHandler)
            .post("/transfer", TransferREST::transferHandler)
            .get("/transactions", TransactionREST::getTransactionsHandler);

    private static void defaultHandler(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Welcome to Revolut Account transfer exercise");
    }

    public static void stop() {
        server.stop();
    }

    public static void main(String[] args) {
        Account.createDemoAccounts(8);
        Transaction.createDummyTransactions();

        server = Undertow.builder()
                .addHttpListener(PORT, HOSTNAME)
                .setHandler(ROUTES)
                .build();
        server.start();
    }
}
