package org.glassfish.tyrus.ball;

import java.io.IOException;
import java.util.HashMap;

import javax.websocket.DeploymentException;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

public class App {

    public static void main(String... args) throws DeploymentException, IOException, InterruptedException {
        Server server = new Server("0.0.0.0", 80, "/", new HashMap<>(), EventEndpoint.class, Sphero.SpheroEndpoint.class);
        HttpServer staticContent = HttpServer.createSimpleServer("/", "0.0.0.0", 8080);
        HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/web/");
        staticContent.getServerConfiguration().addHttpHandler(httpHandler, "/");
        staticContent.start();
        server.start();

        Thread.currentThread().join();
    }
}
