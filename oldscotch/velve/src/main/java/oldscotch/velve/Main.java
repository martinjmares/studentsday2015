package oldscotch.velve;

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

    private final boolean initVelve;

    public Main(boolean initVelve) {
        this.initVelve = initVelve;
    }

    private void run() throws Exception {
        final VelveApp app = new VelveApp(initVelve);
        final ResourceConfig resourceConfig = ResourceConfig.forApplication(app);
        final URI uri = URI.create("http://192.168.1.150:3333/");
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
            if (args.length != 1) {
                System.out.println("USAGE: velve <true|false>");
                System.exit(1);
            }
            Main m = new Main(Boolean.parseBoolean(args[0]));
            m.run();
        } catch (Exception e) {
            LOGGER.error("Ooooops", e);
        }
    }
}
