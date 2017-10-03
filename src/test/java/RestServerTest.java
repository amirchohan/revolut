import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestServerTest {

    private HttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        RestServer.main(null);
        httpClient = new HttpClient();
        httpClient.start();
    }

    @After
    public void tearDown() throws Exception {
        RestServer.stop();
        httpClient.stop();
    }

    @Test
    public void testRestServer() throws Exception {
        ContentResponse response = httpClient.GET("http://localhost:8080");
        Assert.assertEquals("Welcome to Revolut Account transfer exercise", response.getContentAsString());
    }
}