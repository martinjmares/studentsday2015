package oldscotch.growl;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import com.google.code.jgntp.Gntp;
import com.google.code.jgntp.GntpApplicationInfo;
import com.google.code.jgntp.GntpClient;
import com.google.code.jgntp.GntpErrorStatus;
import com.google.code.jgntp.GntpListener;
import com.google.code.jgntp.GntpNotification;
import com.google.code.jgntp.GntpNotificationInfo;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class Growler {

    public static final String APPLICATION_ICON = "/duke.png";

    private static final Logger logger = LoggerFactory.getLogger(Growler.class);

    private final GntpClient client;
    private final GntpNotificationInfo tweetMessage;
    private final AtomicInteger counter = new AtomicInteger(1);

    public Growler() throws IOException, InterruptedException {
        GntpApplicationInfo info = Gntp.appInfo("Twitter").icon(getImage(APPLICATION_ICON)).build();
        client = Gntp.client(info).withTcp().onPort(23053).withPassword("passme").listener(new GntpListener() {
            @Override
            public void onRegistrationSuccess() {
                logger.info("Registered");
            }

            @Override
            public void onNotificationSuccess(GntpNotification notification) {
                logger.info("Notification success: " + notification);
            }

            @Override
            public void onClickCallback(GntpNotification notification) {
                logger.info("Click callback: " + notification.getContext());
            }

            @Override
            public void onCloseCallback(GntpNotification notification) {
                logger.info("Close callback: " + notification.getContext());
            }

            @Override
            public void onTimeoutCallback(GntpNotification notification) {
                logger.info("Timeout callback: " + notification.getContext());
            }

            @Override
            public void onRegistrationError(GntpErrorStatus status, String description) {
                logger.info("Registration Error: " + status + " - desc: " + description);
            }

            @Override
            public void onNotificationError(GntpNotification notification, GntpErrorStatus status, String description) {
                logger.info("Notification Error: " + status + " - desc: " + description);
            }

            @Override
            public void onCommunicationError(Throwable t) {
                logger.error("Communication error", t);
            }
        }).build();
        tweetMessage = Gntp.notificationInfo(info, "New Tweet").build();
        client.register();
        client.notify(Gntp.notification(tweetMessage, "Registered")
                              .text("Our growl support is just registered")
                              .build(), 3, TimeUnit.SECONDS);
    }

    private RenderedImage getImage(String name) throws IOException {
        InputStream is = getClass().getResourceAsStream(name);
        try {
            return ImageIO.read(is);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public void tweet(String message) {
        try {
            client.notify(Gntp.notification(tweetMessage, "Tweet No. " + counter.getAndIncrement())
                                  .text(message)
                                  .build(), 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("Cannot growl new tweet!", e);
        }
    }

}
