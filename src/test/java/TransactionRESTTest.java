import io.undertow.util.Headers;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TransactionRESTTest {
    private HttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        DB.cleanDB();
        RestServer.main(new String[]{"-demo"});
        httpClient = new HttpClient();
        httpClient.start();
    }

    @After
    public void tearDown() throws Exception {
        RestServer.stop();
        httpClient.stop();
    }

    @Test
    public void getTransactionsHandler() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080/transactions");
        Assert.assertEquals("200", response.getHeaders().get(Headers.STATUS_STRING));

        List<Transaction> transactionList = Transaction.fromJsonList(response.getContentAsString());
        Assert.assertTrue(transactionList.size() > 0);
    }

}