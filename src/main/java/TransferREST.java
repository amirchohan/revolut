import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

class TransferREST {

    static void transferHandler(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(((httpServerExchange, s) -> {

            Transaction transaction;
            try {
                transaction = Transaction.fromJson(s);
            } catch (Exception e) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Invalid request: Please send sourceAccId, destAccId and amount");
                return;
            }

            if (!DB.checkIfAccountExists(transaction.getDestAccId()) ||
                    !DB.checkIfAccountExists(transaction.getSourceAccId())) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Error: Destination/Source Account doesn't exist");
                return;
            }

            try {
                transaction.execute();
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 202);

                String response = "{\r\n" +
                        "sourceAcc: " + DB.getAccount(transaction.getSourceAccId()).toJson() + ",\r\n" +
                        "destAcc: " + DB.getAccount(transaction.getDestAccId()).toJson() + "\r\n" +
                        "}";

                httpServerExchange.getResponseSender().send(response);

            } catch (Exception e) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send(e.getMessage());
            }
        }));
    }

}
