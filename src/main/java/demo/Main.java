package demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main {
    private static ApiClient apiClient;

    public static void main(String[] args) {
        apiClient = new ApiClient();
        try {
            createAccount();
            getAccountByNumber();
            updateAccount();
            listAccounts();
            deleteAccount();

            String transferId = createTransfer();
            getTransferById(transferId);
            listTransfers();
            listTransfersByAccountNumber();
            deleteTransfer(transferId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAccount() throws Exception {
        JsonObject account = JsonHelper.getAccount("001", "10000.00");
        Response response = apiClient.sendPOST("accounts", account.toString());
        printRequest("POST accounts", account, response);
    }

    private static void getAccountByNumber() throws Exception {
        Response response = apiClient.sendGET("accounts/001", null);
        printRequest("GET accounts/001", null, response);
    }

    private static void updateAccount() throws Exception {
        JsonObject account = JsonHelper.getAccount("001", "20000.00");
        Response response = apiClient.sendPUT("accounts/001", account.toString());
        printRequest("PUT accounts/001", account, response);
    }

    private static void deleteAccount() throws Exception {
        Response response = apiClient.sendDELETE("accounts/001");
        printRequest("DELETE accounts/001", null, response);
    }

    private static void listAccounts() throws Exception {
        Response response = apiClient.sendGET("accounts", null);
        printRequest("GET accounts", null, response);
    }

    private static String createTransfer() throws Exception {
        JsonObject account1 = JsonHelper.getAccount("002", "10000.00");
        apiClient.sendPOST("accounts", account1.toString());
        JsonObject account2 = JsonHelper.getAccount("003", "10000.00");
        apiClient.sendPOST("accounts", account2.toString());

        JsonObject transfer = JsonHelper.getTransfer("002", "003", "500.00");
        Response response = apiClient.sendPOST("transfers", transfer.toString());
        printRequest("POST transfers", transfer, response);

        JsonObject receivedObject = new Gson().fromJson(response.getBody(), JsonObject.class);
        return !receivedObject.has("id") ? null : receivedObject.get("id").getAsString();
    }

    private static void getTransferById(String transferId) throws Exception {
        Response response = apiClient.sendGET("transfers/" + transferId, null);
        printRequest("GET transfers/" + transferId, null, response);
    }

    private static void deleteTransfer(String transferId) throws Exception {
        Response response = apiClient.sendDELETE("transfers/" + transferId);
        printRequest("DELETE transfers/" + transferId, null, response);
    }

    private static void listTransfers() throws Exception {
        Response response = apiClient.sendGET("transfers", null);
        printRequest("GET transfers", null, response);
    }

    private static void listTransfersByAccountNumber() throws Exception {
        Response response = apiClient.sendGET("transfers?accountNumber=002", null);
        printRequest("GET transfers?accountNumber=002", null, response);
    }

    private static void printRequest(String request, JsonObject data, Response response) {
        System.out.println(request);
        if (data != null) System.out.println(data.toString());
        System.out.print("Response: ");
        System.out.println(response.getCode());
        if (!response.getBody().equals("")) System.out.println(response.getBody());
        System.out.println();
    }
}
