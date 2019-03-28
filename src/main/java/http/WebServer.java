package http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private int port = 8080;
    private Router router;

    public void start() throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.createContext("/", router::handleRequest);
        server.start();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
}
