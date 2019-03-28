import com.google.gson.JsonObject;
import demo.ApiClient;
import demo.JsonHelper;
import demo.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountApiTest {

    private ApiClient apiClient;

    @Before
    public void setUp() throws Exception {
        apiClient = new ApiClient();
    }

    @Test
    public void checkAccountCreate() throws Exception {
        JsonObject account = JsonHelper.getAccount("1", "1000.00");
        Response response = apiClient.sendPOST("accounts", account.toString());
        assertEquals(201, response.getCode());
        assertEquals(account.toString(), response.getBody());
        // clear DB
        apiClient.sendDELETE("accounts/1");
    }

    @Test
    public void checkAccountGetByNumber() throws Exception {
        // create an account
        apiClient.sendPOST("accounts", JsonHelper.getAccount("2", "100.00").toString());
        // get the account by number
        Response response = apiClient.sendGET("accounts/2", null);
        assertEquals(200, response.getCode());
        // clear DB
        apiClient.sendDELETE("accounts/2");
    }

    @Test
    public void checkAccountUpdate() throws Exception {
        // create an account
        apiClient.sendPOST("accounts", JsonHelper.getAccount("3", "1000.00").toString());
        // update the account
        JsonObject updatedAccount = JsonHelper.getAccount("3", "1500.00");
        Response response = apiClient.sendPUT("accounts/3", updatedAccount.toString());
        assertEquals(200, response.getCode());
        assertEquals(updatedAccount.toString(), response.getBody());
        // clear DB
        apiClient.sendDELETE("accounts/3");

    }

    @Test
    public void checkAccountDelete() throws Exception {
        // create an account
        apiClient.sendPOST("accounts", JsonHelper.getAccount("4", "1000.00").toString());
        // delete the account
        Response response = apiClient.sendDELETE("accounts/4");

        assertEquals(200, response.getCode());
        Response checkResponse = apiClient.sendGET("accounts/4", null);
        assertEquals(404, checkResponse.getCode());
    }

    @Test
    public void checkAccountNotFound() throws Exception {
        Response response = apiClient.sendGET("accounts/5", null);
        assertEquals(404, response.getCode());
    }

}