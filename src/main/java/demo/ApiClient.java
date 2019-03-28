package demo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ApiClient {
    private String host = "http://localhost:8080/";

    public Response sendGET(String path, Map<String, String> params) throws IOException {
        if (params != null) {
            StringBuilder paramsStr = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsStr.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                paramsStr.append("=");
                paramsStr.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                paramsStr.append("&");
            }
            path = path + "?" + paramsStr.toString();
        }

        return sendRequest(path , "GET", "");
    }

    public Response sendPOST(String path, String body) throws Exception {
        return sendRequest(path, "POST", body);
    }

    public Response sendPUT(String path, String body) throws IOException {
        return sendRequest(path, "PUT", body);
    }

    public Response sendDELETE(String path) throws IOException {
        return sendRequest(path, "DELETE", "");
    }

    private Response sendRequest(String query, String method, String body) throws IOException {
        URL url = new URL(host + query);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            if (!method.equals("GET")) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(body);
                wr.close();
            }
            StringBuilder responseBody = new StringBuilder();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                boolean addNewLine = false;
                while ((line = rd.readLine()) != null) {
                    if (addNewLine) {
                        responseBody.append('\r');
                    }
                    addNewLine = true;
                    responseBody.append(line);
                }
                rd.close();
            }
            return new Response(responseCode, responseBody.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
