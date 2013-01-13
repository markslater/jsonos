package jsonos;

import org.junit.Test;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.message.header.UDADeviceTypeHeader;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

public class ConnectionTest {
    @Test
    public void canDoSomething() throws Exception {
        // UPnP discovery is asynchronous, we need a callback
        RegistryListener listener = new RegistryListener() {

            public void remoteDeviceDiscoveryStarted(Registry registry,
                                                     RemoteDevice device) {
                System.out.println(
                        "Discovery started: " + device.getDisplayString()
                );
            }

            public void remoteDeviceDiscoveryFailed(Registry registry,
                                                    RemoteDevice device,
                                                    Exception ex) {
                System.out.println(
                        "Discovery failed: " + device.getDisplayString() + " => " + ex
                );
            }

            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                System.out.println(
                        "Remote device available: " + device.getDisplayString()
                );
                if ("ZonePlayer".equals(device.getType().getType())) {
                    System.out.println("device.getServices() = " + device.getServices());
                }
            }

            public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
                System.out.println(
                        "Remote device updated: " + device.getDisplayString()
                );
            }

            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                System.out.println(
                        "Remote device removed: " + device.getDisplayString()
                );
            }

            public void localDeviceAdded(Registry registry, LocalDevice device) {
                System.out.println(
                        "Local device added: " + device.getDisplayString()
                );
            }

            public void localDeviceRemoved(Registry registry, LocalDevice device) {
                System.out.println(
                        "Local device removed: " + device.getDisplayString()
                );
            }

            public void beforeShutdown(Registry registry) {
                System.out.println(
                        "Before shutdown, the registry has devices: "
                                + registry.getDevices().size()
                );
            }

            public void afterShutdown() {
                System.out.println("Shutdown of registry complete!");
            }
        };

        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        UpnpService upnpService = new UpnpServiceImpl(listener);

        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new STAllHeader());

        // Let's wait 10 seconds for them to respond
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000);

        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }

    @Test
    public void findsZonePlayers() throws Exception {
        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        final MyDefaultRegistryListener myDefaultRegistryListener = new MyDefaultRegistryListener();
        final UpnpService upnpService = new UpnpServiceImpl(myDefaultRegistryListener);

        myDefaultRegistryListener.upnpService = upnpService; // TODO this is totally non-threadsafe


        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("ZonePlayer")));

        // Let's wait 10 seconds for them to respond
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000);

        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {
        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        final AlarmRegistryListener alarmRegistryListener = new AlarmRegistryListener();
        final UpnpService upnpService = new UpnpServiceImpl(alarmRegistryListener);

        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new UDADeviceTypeHeader(new UDADeviceType("ZonePlayer")));


        // Let's wait 10 seconds for them to respond
        final AlarmDetailsListener alarmDetailsListener = new AlarmDetailsListener() {
            @Override
            public void gotDetails(String details) {
                System.out.println("details = " + details);
            }

            @Override
            public void failedToGetDetails(String detailsMessage) {
                System.out.println("detailsMessage = " + detailsMessage);
            }
        };
        try (final AlarmPollingService alarmPollingService = new AlarmPollingService(upnpService, alarmRegistryListener, alarmDetailsListener)) {
            alarmPollingService.start();
            System.out.println("Waiting 10 seconds before shutting down...");
            Thread.sleep(10000);
        }

        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }

    private static class MyDefaultRegistryListener extends DefaultRegistryListener {
        private UpnpService upnpService;

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            if (!"ZonePlayer".equals(device.getType().getType())) {
                System.err.println("A non-ZonePlayer responded: " + device.getType());
            } else {
                System.out.println("device.getDisplayString() = " + device.getDisplayString());
                Service service = device.findService(new UDAServiceId("AlarmClock"));
                Action listAlarmsAction = service.getAction("ListAlarms");
                ActionInvocation getStatusInvocation = new ActionInvocation(listAlarmsAction);

                ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        ActionArgumentValue status = invocation.getOutput("CurrentAlarmList");
                        System.out.println("status = " + status);
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        System.err.println(defaultMsg);
                    }
                };

                upnpService.getControlPoint().execute(getStatusCallback);
            }
        }
    }
}
