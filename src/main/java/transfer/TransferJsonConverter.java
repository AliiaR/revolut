package transfer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class TransferJsonConverter implements JsonSerializer<Transfer>, JsonDeserializer<Transfer> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    public TransferJsonConverter() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Override
    public JsonElement serialize(Transfer transfer, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("id", transfer.getId());
        object.addProperty("senderAccount", transfer.getSenderAccount());
        object.addProperty("recipientAccount", transfer.getRecipientAccount());
        object.addProperty("amount", transfer.getAmount().toString());
        object.addProperty("time", dateFormat.format(transfer.getTime()));
        return object;
    }

    @Override
    public Transfer deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Transfer transfer = new Transfer();
        try {
            JsonObject object = json.getAsJsonObject();
            transfer.setId(object.has("id") ? object.get("id").getAsString() : null);
            transfer.setSenderAccount(object.get("senderAccount").getAsString());
            transfer.setRecipientAccount(object.get("recipientAccount").getAsString());
            transfer.setAmount(new BigDecimal(object.get("amount").getAsString()));
            transfer.setTime(object.has("time") ? dateFormat.parse(object.get("time").getAsString()) : null);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
        return transfer;
    }
}
