import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class RestServer {

    private static int PORT = 8080;
    private static String HOSTNAME = "localhost";

    private static void defaultHandler(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Hello World");
    }

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(PORT, HOSTNAME, RestServer::defaultHandler)
                .build();
        server.start();
    }
}
