package oldscotch.twitter;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final int port;

    public Main(int port) {
        this.port = port;
    }

    private void run() throws Exception {
        final TwitterApp app = new TwitterApp();
        final ResourceConfig resourceConfig = ResourceConfig.forApplication(app);
        final URI uri = URI.create("http://localhost:" + port + "/");
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Shutting down!");
                server.shutdown();
                LOGGER.info("STOPPED!");
            }
        }));
        server.start();
        LOGGER.info("======= STARTED!");
        app.getLatch().await();
    }

    public static void main(String[] args) {
        try {
            //Args
            if (args.length != 2) {
                System.out.println("USAGE: twitter <port> <twitter_hash>");
                System.exit(1);
            }
            Main m = new Main(Integer.parseInt(args[0]));
            m.run();
        } catch (Exception e) {
            LOGGER.error("Ooooops", e);
        }
    }
}
