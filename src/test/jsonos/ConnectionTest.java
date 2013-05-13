package jsonos;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import org.junit.BeforeClass;
import org.junit.Test;
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

public class ConnectionTest {

    @BeforeClass
    public static void setLogLevel() {
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

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {

        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        final UpnpService upnpService = new UpnpServiceImpl(new AlarmRegistryListener(new SysOutAlarmsListener()));

        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("ZonePlayer")));

        Thread.sleep(20000);
        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }

}
