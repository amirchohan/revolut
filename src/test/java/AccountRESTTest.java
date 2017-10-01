import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

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
    public void createAccountHandler() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080/account/create");
        Account createdAccount = Account.fromJson(response.getContentAsString());

        Assert.assertTrue(createdAccount.getId() > 0);
        Assert.assertEquals(new BigDecimal("0"), createdAccount.getBalance());
        Assert.assertTrue(createdAccount.getTransactions().size() == 0);
    }

}