package oldscotch.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class LoggingListener implements TweetListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingListener.class);

    @Override
    public void message(String author, String message) {
        LOGGER.info(author + ": " + message);
    }
}
