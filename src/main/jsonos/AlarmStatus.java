package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public abstract class AlarmStatus {
    private static final UDAServiceId ALARM_CLOCK = new UDAServiceId("AlarmClock");

    private AlarmStatus() {
    }

    abstract AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener);

    static AlarmStatus noDevicesKnown() {
        return new NoDevicesKnownAlarmStatus();
    }

    private static final class NoDevicesKnownAlarmStatus extends AlarmStatus {
        @Override
        public AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener) {
            return new DeviceKnownButNoAlarmsSubscriptions(asList(device), upnpService, alarmDetailsListener);
        }
    }

    private static final class DeviceKnownButNoAlarmsSubscriptions extends AlarmStatus {
        private final Iterable<Device> devices;

        public DeviceKnownButNoAlarmsSubscriptions(final Iterable<Device> devices, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            this.devices = new ArrayList<Device>() {{
                for (final Device device : devices) {
                    add(device);
                }
            }};

            // TODO - this only ought to happen if we don't have an existing subscription.
            Service service = devices.iterator().next().findService(ALARM_CLOCK);
            upnpService.getControlPoint().execute(new AlarmDetailsEmittingSubscriptionCallback(service, upnpService, alarmDetailsListener));
        }

        @Override
        public AlarmStatus deviceAdded(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            return new DeviceKnownButNoAlarmsSubscriptions(new ArrayList<Device>() {{
                for (Device existingDevice : devices) {
                    add(existingDevice);
                }
                add(device);
            }}, upnpService, alarmDetailsListener);
        }

    }

}
