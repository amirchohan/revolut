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

import java.util.Random;

public class TransferRESTTest {

    private HttpClient httpClient;

    private Account acc_A, acc_B;

    @Before
    public void setUp() throws Exception {
        DB.cleanDB();
        RestServer.main(null);
        httpClient = new HttpClient();
        httpClient.start();

        //create accounts
        ContentResponse response = httpClient.GET("http://localhost:8080/account/create");
        acc_A = Account.fromJson(response.getContentAsString());
        response = httpClient.GET("http://localhost:8080/account/create");
        acc_B = Account.fromJson(response.getContentAsString());

        //deposit some money into it
        Request httpRequest = httpClient.POST("http://localhost:8080/account/deposit");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        String request = "{\n" +
                "\"destAccId\": " + acc_A.getId() + ",\n" +
                "\"amount\": 150\n" +
                "}";
        httpRequest.content(new StringContentProvider(request), "application/json");
        httpRequest.send();
    }

    @After
    public void tearDown() throws Exception {
        RestServer.stop();
        httpClient.stop();
    }



    @Test
    public void transferHandler_valid() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        String request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 15\n" +
                "}";
        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("202", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertTrue(response.getContentAsString().contains("sourceAcc: {\r\n  \"id\" : "+acc_A.getId()+",\r\n" +
                "  \"balance\" : 135,"));
        Assert.assertTrue(response.getContentAsString().contains("destAcc: {\r\n  \"id\" : "+acc_B.getId()+",\r\n" +
                "  \"balance\" : 15,"));
    }


    @Test
    public void transferHandler_badRequest() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        String request = "}";
        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid request: Please send sourceAccId, destAccId and amount", response.getContentAsString());

        //adding an extra comma
        request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 15,\n" +
                "}";
        httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid request: Please send sourceAccId, destAccId and amount", response.getContentAsString());
    }


    @Test
    public void transferHandler_badAmount() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        //transferring more than available funds
        String request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 151\n" +
                "}";
        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Insufficient funds", response.getContentAsString());

        //transferring negative amount
        request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": -15\n" +
                "}";
        httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid transaction: Transfer amount must be positive", response.getContentAsString());


        //transferring 0 amount
        request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 0\n" +
                "}";
        httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Invalid transaction: Transfer amount must be positive", response.getContentAsString());
    }


    @Test
    public void transferHandler_badAccounts() throws Exception {
        Request httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");

        //Destination account is -1
        String request = "{\n" +
                "\"sourceAccId\": " + acc_A.getId() + ",\n" +
                "\"destAccId\": -1,\n" +
                "\"amount\": 15\n" +
                "}";
        httpRequest.content(new StringContentProvider(request), "application/json");
        ContentResponse response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Error: Destination/Source Account doesn't exist", response.getContentAsString());

        //Source account is -1
        request = "{\n" +
                "\"sourceAccId\": -1,\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 15\n" +
                "}";
        httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Error: Destination/Source Account doesn't exist", response.getContentAsString());


        Random rand = new Random();
        int id;
        do {
            id = rand.nextInt(Integer.MAX_VALUE);
        } while (DB.checkIfAccountExists(id));

        //Account doesn't exist
        request = "{\n" +
                "\"sourceAccId\": " + id + ",\n" +
                "\"destAccId\": " + acc_B.getId() + ",\n" +
                "\"amount\": 15\n" +
                "}";
        httpRequest = httpClient.POST("http://localhost:8080/transfer");
        httpRequest.header(HttpHeader.ACCEPT, "application/json");
        httpRequest.header(HttpHeader.CONTENT_TYPE, "application/json");
        httpRequest.content(new StringContentProvider(request), "application/json");
        response = httpRequest.send();

        Assert.assertEquals("400", response.getHeaders().get(Headers.STATUS_STRING));
        Assert.assertEquals("Error: Destination/Source Account doesn't exist", response.getContentAsString());
    }


}