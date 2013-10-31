package net.sourceforge.jsonos.pi.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.sourceforge.jsonos.RememberingAlarmsListener;
import net.sourceforge.jsonos.SonosClient;
import net.sourceforge.jsonos.Stoppable;
import net.sourceforge.jsonos.SysErrUnexpectedEventsListener;

import static com.pi4j.io.gpio.PinPullResistance.PULL_UP;
import static com.pi4j.io.gpio.RaspiPin.GPIO_00;

public final class PiAlarmSnooze {

    public PiAlarmSnooze() {
    }

    public Stoppable start() {
        final GpioController gpioController = GpioFactory.getInstance();
        final RememberingAlarmsListener alarmsListener = new RememberingAlarmsListener();
        final SonosClient sonosClient = new SonosClient(alarmsListener, new SysErrUnexpectedEventsListener());

        gpioController.provisionDigitalInputPin(GPIO_00, PULL_UP).addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState().isLow()) {
                    sonosClient.snooze();
                }
            }
        });

        gpioController.provisionDigitalInputPin(RaspiPin.GPIO_01, PULL_UP).addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState().isLow()) {
                    sonosClient.stop();
                }
            }
        });

        return new Stoppable() {
            @Override
            public void close() {
                gpioController.shutdown();
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        final Stoppable start = new PiAlarmSnooze().start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                start.close();
            }
        });
    }
}
