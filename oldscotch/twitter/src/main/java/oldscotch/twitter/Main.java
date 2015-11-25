package oldscotch.twitter;

import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusAdapter;

/**
 */
public class Main extends StatusAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private final int port;
    private final List<TweetListener> listeners = new ArrayList<>();
    private volatile Twitter4jStatusClient client;
    private final BroadcasterListener broadcasterListener = new BroadcasterListener();

    public Main(int port) {
        LOGGER.info("Port is " + port);
        this.port = port;
        listeners.add(new FwdToVelve());
        listeners.add(new LoggingListener());
        listeners.add(broadcasterListener);
    }

    BroadcasterListener getBroadcasterListener() {
        return broadcasterListener;
    }

    private void run() throws Exception {
        final TwitterApp app = new TwitterApp(this);
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

    private void readTwitter(Properties props, String keyword) {
        LOGGER.info("Start twitter for keyword: " + keyword);
        final BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

        final Authentication auth = new OAuth1(
                props.getProperty("twitter.consumer.secret"),
                props.getProperty("twitter.consumer.key"),
                props.getProperty("twitter.token.secret"),
                props.getProperty("twitter.token.key"));

        List<String> keyList = new ArrayList<>(1);
        keyList.add(keyword);

        // Create a new BasicClient. By default gzip is enabled.
        final ClientBuilder builder = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(new StatusesFilterEndpoint().trackTerms(keyList))
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue));

        // Wrap our BasicClient with the twitter4j client
        client = new Twitter4jStatusClient(
                builder.build(),
                queue,
                Collections.singletonList(this),
                Executors.newCachedThreadPool());

        client.connect();
        client.process();
    }

    public void fireMessage(String author, String message) {
        for (TweetListener listener : listeners) {
            EXECUTOR_SERVICE.submit(() -> listener.message(author, message));
        }
    }

    @Override
    public void onStatus(final Status status) {
        fireMessage(status.getUser().getName(), status.getText());
    }

    public static void main(String[] args) {
        try {
            //Args
            if (args.length != 3) {
                System.out.println("USAGE: twitter <port> <twitter_hash> <twitter_credentials_properties>");
                System.exit(1);
            }
            Properties props = new Properties();
            props.load(new FileInputStream(args[2]));
            Main m = new Main(Integer.parseInt(args[0]));
            m.readTwitter(props, args[1]);
            m.run();
        } catch (Exception e) {
            LOGGER.error("Ooooops", e);
        }
    }
}
