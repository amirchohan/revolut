import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.List;

class AccountREST {

    static void accountCreateHandler(HttpServerExchange exchange) {
        Account newAccount = new Account();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(Headers.STATUS, 201);
        exchange.getResponseSender().send(newAccount.toJson());
    }

    static void getAccountHandler(HttpServerExchange exchange) {
        try {
            String stingAccId = exchange.getQueryParameters().get("accId").getFirst().split("=")[1];
            int accId = Integer.parseInt(stingAccId);

            if (!DB.checkIfAccountExists(accId)) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseHeaders().put(Headers.STATUS, 400);
                exchange.getResponseSender().send("Error: Please enter a valid account number");
            }

            Account requestedAccount = DB.getAccount(accId);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseHeaders().put(Headers.STATUS, 200);
            exchange.getResponseSender().send(requestedAccount.toJson());
        }
        catch (Exception e) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseHeaders().put(Headers.STATUS, 400);
            exchange.getResponseSender().send("Error: Please enter a valid account number");
        }
    }


    public static void getAccountsHandler(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().put(Headers.STATUS, 200);

        List<Account> accounts = DB.getAccounts();
        exchange.getResponseSender().send(Account.listToJson(accounts));
    }

    static void accountDepositHandler(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(((httpServerExchange, s) -> {

            Transaction depositTransaction;
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

    static void accountWithdrawalHandler(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(((httpServerExchange, s) -> {

            Transaction withdrawalTransaction;
            try {
                withdrawalTransaction = Transaction.fromJson(s);
            } catch (Exception e) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Invalid request: Please send sourceAccId and amount");
                return;
            }

            if (!DB.checkIfAccountExists(withdrawalTransaction.getSourceAccId())) {
                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                httpServerExchange.getResponseHeaders().put(Headers.STATUS, 400);
                httpServerExchange.getResponseSender().send("Error: Account doesn't exist");
                return;
            }
            withdrawalTransaction.setDestAccId(-1);

            try {
                Account account = DB.getAccount(withdrawalTransaction.getSourceAccId());
                account.withdraw(withdrawalTransaction);

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
