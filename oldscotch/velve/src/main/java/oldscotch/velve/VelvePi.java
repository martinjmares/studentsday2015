package oldscotch.velve;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class VelvePi implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelvePi.class);
    private static final long OPEN_ITERVAL = 1000l;

    private final AtomicInteger openCount = new AtomicInteger(0);
    private final AtomicBoolean stop = new AtomicBoolean(false);
    private final GpioPinDigitalOutput velve;

    public VelvePi() {
        final GpioController gpio = GpioFactory.getInstance();
        velve = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Velve", PinState.LOW);
        LOGGER.info("VELVE HW initialized.");
    }

    public void run() {
        while (!stop.get()) {
            while (openCount.get() > 0) {
                openCount.decrementAndGet();
                LOGGER.info("Little more ...");
                velve.high();
                try {
                    Thread.sleep(OPEN_ITERVAL);
                } catch (InterruptedException e) {
                    LOGGER.error("Cannot sleep!");
                }
                velve.low();
            }
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread was interupted");
                    return;
                }
            }
        }
    }

    public void add() {
        openCount.incrementAndGet();
        synchronized (this) {
            notify();
        }
    }
}
