package jsonos;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class AlarmRegistryListener extends DefaultRegistryListener {

    private static final DeviceType ZONE_PLAYER = new UDADeviceType("ZonePlayer");

    private final Object lock = new Object();
    private AlarmStatus alarmStatus = noDevicesKnown();

    static AlarmStatus noDevicesKnown() {
        return new NoDevicesKnownAlarmStatus();
    }

    static AlarmStatus deviceKnownButNoAlarmsKnown(final Device device) {
        return new DeviceKnownButNoAlarmsKnownAlarmStatus(asList(device));
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
        public AlarmStatus deviceAdded(final Device device) {
            return deviceKnownButNoAlarmsKnown(device);
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
    }
}
