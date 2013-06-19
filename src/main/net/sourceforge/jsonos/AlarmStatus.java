package net.sourceforge.jsonos;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UDAServiceId;

import java.util.ArrayList;

public abstract class AlarmStatus {
    private static final UDAServiceId ALARM_CLOCK = new UDAServiceId("AlarmClock");

    private AlarmStatus() {
    }

    abstract AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener);

    abstract void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Alarm alarm);

    static AlarmStatus noDevicesKnown() {
        return new NoDevicesKnownAlarmStatus();
    }

    private static final class NoDevicesKnownAlarmStatus extends AlarmStatus {
        @Override
        public AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownButNoAlarmsSubscriptions(device, upnpService, alarmDetailsListener, unexpectedEventsListener);
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Alarm alarm) {
            //To change body of implemented methods use File | Settings | File Templates.
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
        AlarmStatus deviceAdded(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                for (Device existingDevice : devices) {
                    add(existingDevice);
                }
                add(device);
            }}, upnpService, alarmDetailsListener);
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Alarm alarm) {
            for (Device device : devices) {
                snooze(upnpService, unexpectedEventsListener, device, alarm);
            }
        }
    }

    private static final class DeviceKnownButNoAlarmsSubscriptions extends AlarmStatus {
        private final Device device;

        DeviceKnownButNoAlarmsSubscriptions(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
            this.device = device;
            Service service = device.findService(ALARM_CLOCK);
            upnpService.getControlPoint().execute(new AlarmDetailsEmittingSubscriptionCallback(service, upnpService, alarmDetailsListener, unexpectedEventsListener));
        }

        @Override
        public AlarmStatus deviceAdded(final Device newDevice, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                add(device);
                add(newDevice);
            }}, upnpService, alarmDetailsListener);
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Alarm alarm) {
            snooze(upnpService, unexpectedEventsListener, device, alarm);
        }

    }

    static void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Device device, final Alarm alarm) {
        final Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
        Action action = avTransportService.getAction("SnoozeAlarm");
        final ActionInvocation invocation = new ActionInvocation(action);
        Period snoozePeriod = Period.minutes(2);
        PeriodFormatter pFormatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();

        try {
            invocation.setInput("Duration", pFormatter.print(snoozePeriod));
        } catch (InvalidValueException e) {
            unexpectedEventsListener.invalidValueSettingSnoozePeriod(e);
        }
        new ActionCallback.Default(invocation, upnpService.getControlPoint()).run();
        ActionException anException = invocation.getFailure();
        if (anException != null && anException.getMessage() != null) {
            System.out.println("anException.getMessage() = " + anException.getMessage());
        }
    }

}
