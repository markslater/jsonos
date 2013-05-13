package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceId;

import java.util.ArrayList;

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
            return new DeviceKnownButNoAlarmsSubscriptions(device, upnpService, alarmDetailsListener);
        }
    }

    private static final class DeviceKnownAndAlarmSubscriptionsExist extends AlarmStatus {  // TODO - implies we need to handle device removed
        private final Iterable<Device> devices;

        DeviceKnownAndAlarmSubscriptionsExist(final Iterable<Device> devices, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            this.devices = new ArrayList<Device>() {{
                for (final Device device : devices) {
                    add(device);
                }
            }};
        }

        @Override
        AlarmStatus deviceAdded(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                for (Device existingDevice : devices) {
                    add(existingDevice);
                }
                add(device);
            }}, upnpService, alarmDetailsListener);
        }
    }

    private static final class DeviceKnownButNoAlarmsSubscriptions extends AlarmStatus {
        private final Device device;

        DeviceKnownButNoAlarmsSubscriptions(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            this.device = device;
            Service service = device.findService(ALARM_CLOCK);
            upnpService.getControlPoint().execute(new AlarmDetailsEmittingSubscriptionCallback(service, upnpService, alarmDetailsListener));
        }

        @Override
        public AlarmStatus deviceAdded(final Device newDevice, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                add(device);
                add(newDevice);
            }}, upnpService, alarmDetailsListener);
        }

    }

}
