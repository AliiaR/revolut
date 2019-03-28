package http;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    private final Pattern paramPattern = Pattern.compile("\\{([\\w\\d]+)\\}");
    private String endpoint;
    private Pattern endpointPattern;
    private List<String> params;
    private Map<String, String> parsedParams;
    private Map<String, ThrowableConsumer<ExchangeWrapper>> handlerMap;

    public Route(String endpoint) {
        this.endpoint = endpoint;
        handlerMap = new HashMap<>();
        params = new ArrayList<>();

        Matcher matcher = paramPattern.matcher(endpoint);
        StringBuffer endpointPatternStr = new StringBuffer();
        while (matcher.find()) {
            params.add(matcher.group(1));
            matcher.appendReplacement(endpointPatternStr, "([\\\\w\\\\d]+)");
        }
        matcher.appendTail(endpointPatternStr);
        endpointPattern = Pattern.compile(endpointPatternStr.toString());
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public boolean matchURI(URI uri) throws IOException {
        String uriPath = uri.getPath();
        Matcher matcher = endpointPattern.matcher(uriPath);
        if (!matcher.matches()) {
            return false;
        }
        parsedParams = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                int paramName = pair.indexOf("=");
                if (paramName == -1) {
                    continue;
                }
                parsedParams.put(URLDecoder.decode(pair.substring(0, paramName), "UTF-8"),
                        URLDecoder.decode(pair.substring(paramName + 1), "UTF-8"));
            }
        }
        for (int i = 0; i < params.size(); i++) {
            parsedParams.put(params.get(i), matcher.group(i + 1));
        }
        return true;
    }

    public Map<String, String> getURIParams() {
        return parsedParams;
    }

    public void addMethodHandler(String method, ThrowableConsumer<ExchangeWrapper> handler) {
        handlerMap.put(method, handler);
    }

    public ThrowableConsumer<ExchangeWrapper> getMethodHandler(String method) {
        return handlerMap.get(method);
    }
}
