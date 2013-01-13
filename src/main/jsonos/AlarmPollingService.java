package jsonos;

import org.teleal.cling.UpnpService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlarmPollingService implements AutoCloseable {

    private final UpnpService upnpService;
    private final AlarmRegistryListener alarmRegistryListener;
    private final AlarmDetailsListener alarmDetailsListener;
    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

    public AlarmPollingService(final UpnpService upnpService, final AlarmRegistryListener alarmRegistryListener, AlarmDetailsListener alarmDetailsListener) {
        this.upnpService = upnpService;
        this.alarmRegistryListener = alarmRegistryListener;
        this.alarmDetailsListener = alarmDetailsListener;
    }

    public void start() {
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                alarmRegistryListener.getAlarmStatus().alarms(upnpService, alarmDetailsListener);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
}
