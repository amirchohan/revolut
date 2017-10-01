import io.undertow.util.Headers;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class AccountRESTTest {

    private HttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        RestServer.main(null);
        httpClient = new HttpClient();
        httpClient.start();
    }

    @After
    public void tearDown() throws Exception {
        httpClient.stop();
    }


    @Test
    public void accountCreateHandler() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080/account/create");
        Assert.assertEquals("201", response.getHeaders().get(Headers.STATUS_STRING));
        Account createdAccount = Account.fromJson(response.getContentAsString());

        Assert.assertTrue(createdAccount.getId() > 0);
        Assert.assertEquals(new BigDecimal("0"), createdAccount.getBalance());
        Assert.assertTrue(createdAccount.getTransactions().size() == 0);
    }

    @Test
    public void accountDepositHandler() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080/account/create");
        Account createdAccount = Account.fromJson(response.getContentAsString());

        Request httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        String request = "{\n" +
                "\"destAccId\": " + createdAccount.getId() + ",\n" +
                "\"amount\": 150\n" +
                "}";

        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();
        Assert.assertEquals("202", response.getHeaders().get(Headers.STATUS_STRING));

        Account returnedAccount = Account.fromJson(response.getContentAsString());

        String expectedResponse = "{\r\n" +
                "  \"id\" : " + createdAccount.getId() + ",\r\n" +
                "  \"balance\" : 150,\r\n" +
                "  \"transactions\" : [ {\r\n" +
                "    \"id\" : " + returnedAccount.getTransactions().get(0).getId() + ",\r\n" +
                "    \"sourceAccId\" : -1,\r\n" +
                "    \"destAccId\" : " + createdAccount.getId() + ",\r\n" +
                "    \"amount\" : 150,\r\n" +
                "    \"successful\" : true\r\n" +
                "  } ]\r\n" +
                "}";

        Assert.assertEquals(expectedResponse, response.getContentAsString());
    }

    @Test
    public void accountDepositHandler_badAccId() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        String request = "{\n" +
                "\"destAccId\": 6754,\n" +
                "\"amount\": 150\n" +
                "}";

        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Error: Account doesn't exist", response.getContentAsString());
    }

    @Test
    public void accountDepositHandler_badDeposit() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080/account/create");
        Account createdAccount = Account.fromJson(response.getContentAsString());

        Request httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        String request = "{\n" +
                "\"destAccId\": " + createdAccount.getId() + ",\n" +
                "\"amount\": 0\n" +
                "}";

        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid transaction: Deposit amount must be positive", response.getContentAsString());




        request = "{\n" +
                "\"destAccId\": " + createdAccount.getId() + ",\n" +
                "\"amount\": -5\n" +
                "}";

        httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid transaction: Deposit amount must be positive", response.getContentAsString());
    }

    @Test
    public void accountDepositHandler_badRequest() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        String request = "";

        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid request: Please send destAccId and amount", response.getContentAsString());
    }
}