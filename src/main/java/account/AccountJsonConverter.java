package account;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class AccountJsonConverter implements JsonSerializer<Account>, JsonDeserializer<Account> {
    @Override
    public JsonElement serialize(Account account, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("number", account.getNumber());
        object.addProperty("currency", account.getCurrency());
        object.addProperty("balance", account.getBalance().toString());
        return object;
    }

    @Override
    public Account deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Account account = new Account();
        try {
            JsonObject object = json.getAsJsonObject();
            account.setNumber(object.get("number").getAsString());
            account.setCurrency(object.get("currency").getAsString());
            account.setBalance(new BigDecimal(object.get("balance").getAsString()));
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
        return account;
    }
}
