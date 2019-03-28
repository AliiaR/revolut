import com.google.gson.Gson;
import com.google.gson.JsonObject;
import demo.ApiClient;
import demo.JsonHelper;
import demo.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransferApiTest {

    private ApiClient apiClient;

    @Before
    public void setUp() throws Exception {
        apiClient = new ApiClient();
    }

    @Test
    public void checkTransferCreate() throws Exception {
        // create accounts
        apiClient.sendPOST("accounts", JsonHelper.getAccount("6", "1000.00").toString());
        apiClient.sendPOST("accounts", JsonHelper.getAccount("7", "2000.00").toString());
        // create a transfer
        JsonObject transfer = JsonHelper.getTransfer("6", "7", "500.00");
        Response response = apiClient.sendPOST("transfers", transfer.toString());

        assertEquals(201, response.getCode());
        String transferId = getTransferIdFromJson(response.getBody());

        // check the created transfer
        JsonObject result = new Gson().fromJson(response.getBody(), JsonObject.class);
        assertEquals(transfer.get("senderAccount").getAsString(), result.get("senderAccount").getAsString());
        assertEquals(transfer.get("recipientAccount").getAsString(), result.get("recipientAccount").getAsString());
        assertEquals(transfer.get("amount").getAsString(), result.get("amount").getAsString());

        // check accounts balances
        Response account1Response = apiClient.sendGET("accounts/6", null);
        assertEquals(200, account1Response.getCode());
        assertEquals(JsonHelper.getAccount("6", "500.00").toString(), account1Response.getBody());

        Response account2Response = apiClient.sendGET("accounts/7", null);
        assertEquals(200, account2Response.getCode());
        assertEquals(JsonHelper.getAccount("7", "2500.00").toString(), account2Response.getBody());

        // clear DB
        apiClient.sendDELETE("accounts/6");
        apiClient.sendDELETE("accounts/7");
        apiClient.sendDELETE("transfer/" + transferId);
    }

    @Test
    public void checkTransferNotEnoughMoney() throws Exception {
        // create accounts
        JsonObject account1 = JsonHelper.getAccount("6", "10.00");
        apiClient.sendPOST("accounts", account1.toString());
        JsonObject account2 = JsonHelper.getAccount("7", "2000.00");
        apiClient.sendPOST("accounts", account2.toString());
        // create a transfer
        JsonObject transfer = JsonHelper.getTransfer("6", "7", "500.00");
        Response response = apiClient.sendPOST("transfers", transfer.toString());

        assertEquals(422, response.getCode());

        // check accounts balances
        Response account1Response = apiClient.sendGET("accounts/6", null);
        assertEquals(200, account1Response.getCode());
        assertEquals(account1.toString(), account1Response.getBody());

        Response account2Response = apiClient.sendGET("accounts/7", null);
        assertEquals(200, account2Response.getCode());
        assertEquals(account2.toString(), account2Response.getBody());

        // clear DB
        apiClient.sendDELETE("accounts/6");
        apiClient.sendDELETE("accounts/7");
    }

    @Test
    public void checkTransferDelete() throws Exception {
        // create accounts
        JsonObject account1 = JsonHelper.getAccount("10", "1000.00");
        apiClient.sendPOST("accounts", account1.toString());
        JsonObject account2 = JsonHelper.getAccount("11", "2000.00");
        apiClient.sendPOST("accounts", account2.toString());
        //create a transfer
        Response transferResponse = apiClient.sendPOST("transfers",
                JsonHelper.getTransfer("10", "11", "1000.00").toString()
        );
        String transferId = getTransferIdFromJson(transferResponse.getBody());

        // delete the transfer
        Response response = apiClient.sendDELETE("transfers/" + transferId);
        assertEquals(200, response.getCode());
        // get the transfer by id
        Response checkResponse = apiClient.sendGET("transfers/" + transferId, null);
        assertEquals(404, checkResponse.getCode());

        // check accounts balances
        Response account1Response = apiClient.sendGET("accounts/10", null);
        assertEquals(200, account1Response.getCode());
        assertEquals(account1.toString(), account1Response.getBody());
        Response account2Response = apiClient.sendGET("accounts/11", null);
        assertEquals(200, account2Response.getCode());
        assertEquals(account2.toString(), account2Response.getBody());

        // clear DB
        apiClient.sendDELETE("accounts/10");
        apiClient.sendDELETE("accounts/11");
    }

    @Test
    public void checkTransferNotFound() throws Exception {
        String transferId = "100500";
        Response response = apiClient.sendGET("transfers/" + transferId, null);
        assertEquals(404, response.getCode());
    }

    private String getTransferIdFromJson(String transferJson) {
        JsonObject receivedObject = new Gson().fromJson(transferJson, JsonObject.class);
        return !receivedObject.has("id") ? null : receivedObject.get("id").getAsString();
    }
}