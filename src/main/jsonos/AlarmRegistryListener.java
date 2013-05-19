package jsonos;

import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import static jsonos.AlarmStatus.noDevicesKnown;

public final class AlarmRegistryListener extends DefaultRegistryListener { // TODO probably ought to extend registry listener, to force all events to be handled

    private static final DeviceType ZONE_PLAYER = new UDADeviceType("ZonePlayer");

    private final Object lock = new Object();
    private final AlarmDetailsListener alarmDetailsListener;
    private final UnexpectedEventsListener unexpectedEventsListener;
    private AlarmStatus alarmStatus = noDevicesKnown();

    public AlarmRegistryListener(final AlarmsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
        this.unexpectedEventsListener = unexpectedEventsListener;
        this.alarmDetailsListener = new ParsingAlarmDetailsListener(alarmDetailsListener);
    }

    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        if (ZONE_PLAYER.equals(device.getType())) {
            setAlarmStatus(
                    getAlarmStatus().deviceAdded(device, registry.getUpnpService(), alarmDetailsListener, unexpectedEventsListener)
            );
        } else {
            // TODO got a callback from something unexpected
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

}
