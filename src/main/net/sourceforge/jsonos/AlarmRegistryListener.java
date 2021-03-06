package net.sourceforge.jsonos;

import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

public final class AlarmRegistryListener extends DefaultRegistryListener { // TODO probably ought to extend registry listener, to force all events to be handled

    private static final DeviceType ZONE_PLAYER = new UDADeviceType("ZonePlayer");

    private final Object lock = new Object();
    private final AlarmDetailsListener alarmDetailsListener;
    private final AlarmStateListener alarmStateListener;
    private final UnexpectedEventsListener unexpectedEventsListener;
    private AlarmStatus alarmStatus = AlarmStatus.noDevicesKnown();

    public AlarmRegistryListener(final AlarmsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
        this.unexpectedEventsListener = unexpectedEventsListener;
        this.alarmDetailsListener = new ParsingAlarmDetailsListener(alarmDetailsListener);
        this.alarmStateListener = new ParsingAlarmStateListener(new SonosStateListener() {
            @Override
            public void gotState(final boolean alarmRunning) {
                System.out.println("new AvTransportStateVariables(Optional.<Boolean>absent()).parse(lastChange).isAlarmRunning() = " + alarmRunning);
            }

            @Override
            public void failedToGetState(final String detailsMessage) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        if (ZONE_PLAYER.equals(device.getType()) && "ZP120".equals(device.getDetails().getModelDetails().getModelNumber())) {
            setAlarmStatus(
                    getAlarmStatus().deviceAdded(device, registry.getUpnpService(), alarmDetailsListener, alarmStateListener, unexpectedEventsListener)
            );
        } else {
            unexpectedEventsListener.unexpectedDeviceCallback(device);
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
