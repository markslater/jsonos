package net.sourceforge.jsonos;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.UDADeviceTypeHeader;
import org.teleal.cling.model.types.UDADeviceType;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.any;
import static java.util.Arrays.asList;
import static java.util.logging.Level.WARNING;

public final class SonosClient {

    private final AlarmRegistryListener alarmRegistryListener;
    private final UnexpectedEventsListener unexpectedEventsListener;

    static {
        final Logger topLogger = java.util.logging.Logger.getLogger("");
        find(asList(topLogger.getHandlers()), new Predicate<Handler>() {
                    @Override
                    public boolean apply(Handler input) {
                        return input instanceof ConsoleHandler;
                    }
                }, new Supplier<Handler>() {
                    @Override
                    public Handler get() {
                        Handler handler = new ConsoleHandler();
                        topLogger.addHandler(handler);
                        return handler;
                    }
                }
        ).setLevel(WARNING);
    }

    private static <T> T find(Iterable<T> source, Predicate<T> predicate, Supplier<T> defaultSupplier) {
        return (any(source, predicate)) ? Iterables.find(source, predicate) : defaultSupplier.get();
    }

    private final UpnpService upnpService;

    public SonosClient(final AlarmsListener alarmsListener, final UnexpectedEventsListener unexpectedEventsListener) {
        this.unexpectedEventsListener = unexpectedEventsListener;
        alarmRegistryListener = new AlarmRegistryListener(alarmsListener, this.unexpectedEventsListener);
        upnpService = new UpnpServiceImpl(alarmRegistryListener);
        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("ZonePlayer")));
    }

    public void snooze() {
        alarmRegistryListener.getAlarmStatus().snooze(upnpService, unexpectedEventsListener);
    }

    public void sleep() {
        alarmRegistryListener.getAlarmStatus().sleep(upnpService, unexpectedEventsListener);
    }

    public void runningAlarms() {
        alarmRegistryListener.getAlarmStatus().getRunningAlarms(upnpService, unexpectedEventsListener);
    }

    public void stop() {
        alarmRegistryListener.getAlarmStatus().stop(upnpService, unexpectedEventsListener);
    }

    public void close() throws Exception {
        upnpService.shutdown();
    }
}
