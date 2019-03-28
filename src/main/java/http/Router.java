package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private List<Route> routes;

    protected void addGET(String endpoint, ThrowableConsumer<ExchangeWrapper> handler) {
        addEndpointMethodHandler(endpoint, "GET", handler);
    }

    protected void addPOST(String endpoint, ThrowableConsumer<ExchangeWrapper> handler) {
        addEndpointMethodHandler(endpoint, "POST", handler);
    }

    protected void addPUT(String endpoint, ThrowableConsumer<ExchangeWrapper> handler) {
        addEndpointMethodHandler(endpoint, "PUT", handler);
    }

    protected void addDELETE(String endpoint, ThrowableConsumer<ExchangeWrapper> handler) {
        addEndpointMethodHandler(endpoint, "DELETE", handler);
    }

    private void addEndpointMethodHandler(String endpoint, String method, ThrowableConsumer<ExchangeWrapper> handler) {
        Route endpointRoute = null;
        for (Route route: getRoutes()) {
            if (route.getEndpoint().equals(endpoint)) {
                endpointRoute = route;
                break;
            }
        }
        if (endpointRoute == null) {
            endpointRoute = new Route(endpoint);
            routes.add(endpointRoute);
        }
        endpointRoute.addMethodHandler(method, handler);
    }

    public void handleRequest(HttpExchange exchange) throws IOException {
        ExchangeWrapper exchangeWrapper = new ExchangeWrapper(exchange);
        try {
            Route route = findRouteByURI(exchange.getRequestURI());
            if (route == null) {
                onEndpointNotFound(exchangeWrapper);
                exchangeWrapper.sendResponse();
                return;
            }
            exchangeWrapper = new ExchangeWrapper(exchange, route.getURIParams());
            ThrowableConsumer<ExchangeWrapper> methodHandler = route.getMethodHandler(exchange.getRequestMethod());
            if (methodHandler == null) {
                onMethodNotImplemented(exchangeWrapper);
                exchangeWrapper.sendResponse();
                return;
            }
            methodHandler.accept(exchangeWrapper);
            exchangeWrapper.sendResponse();
        } catch (Exception e) {
            onException(exchangeWrapper, e);
        }
        finally {
            exchangeWrapper.sendResponse();
        }
    }

    private Route findRouteByURI(URI uri) throws IOException {
        for (Route route : getRoutes()) {
            if (route.matchURI(uri)) {
                return route;
            }
        }
        return null;
    }

    protected List<Route> getRoutes() {
        if (routes == null) {
            routes = new ArrayList<>();
            initRoutes();
        }
        return routes;
    }

    protected void initRoutes() {

    }

    protected void onEndpointNotFound(ExchangeWrapper exchange) throws Exception {
        exchange.setResponseCode(404);
    }

    protected void onMethodNotImplemented(ExchangeWrapper exchange) throws Exception {
        exchange.setResponseCode(405);
    }

    protected void onException(ExchangeWrapper exchange, Throwable exception) {
        exception.printStackTrace();
        exchange.setResponseCode(500);
    }
}
