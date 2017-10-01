import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;

final class RestServer {

    private static int PORT = 8080;
    private static String HOSTNAME = "localhost";

    private static final HttpHandler ROUTES = new RoutingHandler()
            .get("/", RestServer::defaultHandler)
            .get("/account/create", AccountREST::accountCreateHandler)
            .post("/account/deposit", AccountREST::accountDepositHandler)
            .post("/account/withdraw", AccountREST::accountWithdrawalHandler);

    private static void defaultHandler(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Welcome to Revolut Account transfer exercise");
    }

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(PORT, HOSTNAME)
                .setHandler(ROUTES)
                .build();
        server.start();
    }
}
