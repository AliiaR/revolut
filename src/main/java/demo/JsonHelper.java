package demo;

import com.google.gson.JsonObject;

public class JsonHelper {
    public static JsonObject getAccount(String number, String balance) {
        JsonObject object = new JsonObject();
        object.addProperty("number", number);
        object.addProperty("currency", "GBP");
        object.addProperty("balance", balance);
        return object;
    }

    public static JsonObject getTransfer(String senderAccount, String recipientAccount, String amount) {
        JsonObject object = new JsonObject();
        object.addProperty("senderAccount", senderAccount);
        object.addProperty("recipientAccount", recipientAccount);
        object.addProperty("amount", amount);
        return object;
    }
}
