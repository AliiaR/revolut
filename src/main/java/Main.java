import http.WebServer;

public class Main {

    public static void main(String[] args) {
        final int port = 8080;
        try {
            WebServer webServer = new WebServer();
            webServer.setPort(port);
            webServer.setRouter(new ApiRouter());
            webServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
