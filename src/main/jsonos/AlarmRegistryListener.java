package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class AlarmRegistryListener extends DefaultRegistryListener {

    private static final DeviceType ZONE_PLAYER = new UDADeviceType("ZonePlayer");
    public static final UDAServiceId ALARM_CLOCK = new UDAServiceId("AlarmClock");

    private final Object lock = new Object();
    private AlarmStatus alarmStatus = noDevicesKnown();

    static AlarmStatus noDevicesKnown() {
        return new NoDevicesKnownAlarmStatus();
    }

    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        if (ZONE_PLAYER.equals(device.getType())) {
            setAlarmStatus(getAlarmStatus().deviceAdded(device));
        }
    }

    private void setAlarmStatus(final AlarmStatus alarmStatus) {
        synchronized (lock) {
            this.alarmStatus = alarmStatus;
        }
    }

    public AlarmStatus getAlarmStatus() {
        synchronized (lock) {
            return alarmStatus;
        }
    }

    private static class NoDevicesKnownAlarmStatus implements AlarmStatus {
        @Override
        public String statusString() {
            return "No devices have been found.";
        }

        @Override
        public String alarms(final UpnpService upnpService, AlarmDetailsListener alarmDetailsListener) {
            return "No devices to query for alarms.";
        }

        @Override
        public AlarmStatus deviceAdded(final Device device) {
            return new DeviceKnownButNoAlarmsKnownAlarmStatus(asList(device));
        }
    }

    private static class DeviceKnownButNoAlarmsKnownAlarmStatus implements AlarmStatus {
        private final Iterable<Device> devices;

        public DeviceKnownButNoAlarmsKnownAlarmStatus(final Iterable<Device> devices) {
            this.devices = new ArrayList<Device>() {{
                for (final Device device : devices) {
                    add(device);
                }
            }};
        }

        @Override
        public AlarmStatus deviceAdded(final Device device) {
            return new DeviceKnownButNoAlarmsKnownAlarmStatus(new ArrayList<Device>() {{
                for (Device existingDevice : devices) {
                    add(existingDevice);
                }
                add(device);
            }});
        }

        @Override
        public String statusString() {
            return "A Zone Player has been found: " + devices;
        }

        @Override
        public String alarms(final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            Service service = devices.iterator().next().findService(ALARM_CLOCK);
            Action listAlarmsAction = service.getAction("ListAlarms");
            ActionInvocation getStatusInvocation = new ActionInvocation(listAlarmsAction);
            upnpService.getControlPoint().execute(new ActionCallback(getStatusInvocation) {

                @Override
                public void success(ActionInvocation invocation) {
                    alarmDetailsListener.gotDetails(invocation.getOutput("CurrentAlarmList").toString());
                }

                @Override
                public void failure(ActionInvocation invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    alarmDetailsListener.failedToGetDetails(defaultMsg);
                }
            });
            return null;
        }
    }

}
