package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExchangeWrapper {
    private HttpExchange httpExchange;
    private Map<String, String> uriParams;
    private int responseCode = 200;
    private String responseBody = "";
    private String contentType = "application/json";

    public ExchangeWrapper(HttpExchange httpExchange) {
        this(httpExchange, new HashMap<>());
    }

    public ExchangeWrapper(HttpExchange httpExchange, Map<String, String> uriParams) {
        this.httpExchange = httpExchange;
        this.uriParams = uriParams;
    }

    public void setResponseBody(String rensponseBody) {
        this.responseBody = rensponseBody;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestBody() throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        StringBuilder builder = new StringBuilder();
        while ((length = httpExchange.getRequestBody().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        builder.append(result.toString("UTF-8"));
        return  builder.toString();
    }

    public String getUriParam(String paramName) {
        return getUriParam(paramName, null);
    }

    public String getUriParam(String paramName, String defaultValue) {
        return uriParams == null || !uriParams.containsKey(paramName)
                ? defaultValue : uriParams.get(paramName);
    }

    public void sendResponse() throws IOException {
        byte[] bytes = responseBody.getBytes();
        httpExchange.getResponseHeaders().set("Content-Type", contentType);
        httpExchange.sendResponseHeaders(responseCode, bytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
