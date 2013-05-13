package jsonos;

import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import static jsonos.AlarmStatus.noDevicesKnown;

public final class AlarmRegistryListener extends DefaultRegistryListener {

    private static final DeviceType ZONE_PLAYER = new UDADeviceType("ZonePlayer");

    private final Object lock = new Object();
    private final AlarmDetailsListener alarmDetailsListener;
    private AlarmStatus alarmStatus = noDevicesKnown();

    public AlarmRegistryListener(AlarmsListener alarmDetailsListener) {
        this.alarmDetailsListener = new ParsingAlarmDetailsListener(alarmDetailsListener);
    }

    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        if (ZONE_PLAYER.equals(device.getType())) {
            setAlarmStatus(
                    getAlarmStatus().deviceAdded(device, registry.getUpnpService(), alarmDetailsListener)
            );
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
