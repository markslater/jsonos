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

import static org.joda.time.Period.minutes;

public abstract class AlarmStatus {
    private static final UDAServiceId ALARM_CLOCK = new UDAServiceId("AlarmClock");

    private AlarmStatus() {
    }

    abstract AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener, final AlarmStateListener alarmStateListener, final UnexpectedEventsListener unexpectedEventsListener);

    abstract void stop(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener);

    abstract void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener);

    abstract void sleep(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener);

    abstract void getRunningAlarms(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener);

    static AlarmStatus noDevicesKnown() {
        return new NoDevicesKnownAlarmStatus();
    }

    private static final class NoDevicesKnownAlarmStatus extends AlarmStatus {
        @Override
        public AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener, final AlarmStateListener alarmStateListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownButNoAlarmsSubscriptions(device, upnpService, alarmDetailsListener, alarmStateListener, unexpectedEventsListener);
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        void sleep(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        void getRunningAlarms(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        void stop(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
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
        AlarmStatus deviceAdded(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final AlarmStateListener alarmStateListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                for (Device existingDevice : devices) {
                    add(existingDevice);
                }
                add(device);
            }}, upnpService, alarmDetailsListener);
        }

        @Override
        void stop(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            for (Device device : devices) {
                stop(upnpService, device);
            }
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            for (Device device : devices) {
                snooze(upnpService, unexpectedEventsListener, device);
            }
        }

        @Override
        void sleep(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            for (Device device : devices) {
                sleep(upnpService, unexpectedEventsListener, device);
            }
        }

        @Override
        void getRunningAlarms(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            for (Device device : devices) {
                runningAlarms(upnpService, device);
            }
        }
    }

    private static final class DeviceKnownButNoAlarmsSubscriptions extends AlarmStatus {
        private final Device device;

        DeviceKnownButNoAlarmsSubscriptions(final Device device, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final AlarmStateListener alarmStateListener, final UnexpectedEventsListener unexpectedEventsListener) {
            this.device = device;
            Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
            upnpService.getControlPoint().execute(new AlarmStateEmittingSubscriptionCallback(avTransportService, alarmStateListener, unexpectedEventsListener));
            Service alarmClockService = device.findService(ALARM_CLOCK);
            upnpService.getControlPoint().execute(new AlarmDetailsEmittingSubscriptionCallback(alarmClockService, upnpService, alarmDetailsListener, unexpectedEventsListener));
        }

        @Override
        public AlarmStatus deviceAdded(final Device newDevice, final UpnpService upnpService, final AlarmDetailsListener alarmDetailsListener, final AlarmStateListener alarmStateListener, final UnexpectedEventsListener unexpectedEventsListener) {
            return new DeviceKnownAndAlarmSubscriptionsExist(new ArrayList<Device>() {{
                add(device);
                add(newDevice);
            }}, upnpService, alarmDetailsListener);
        }

        @Override
        void stop(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            stop(upnpService, device);
        }

        @Override
        void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            snooze(upnpService, unexpectedEventsListener, device);
        }

        @Override
        void sleep(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            sleep(upnpService, unexpectedEventsListener, device);
        }

        @Override
        void getRunningAlarms(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener) {
            runningAlarms(upnpService, device);
        }

    }

    static void snooze(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Device device) {
        final Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
//        "GetRunningAlarmProperties";
        Action action = avTransportService.getAction("SnoozeAlarm");
        final ActionInvocation invocation = new ActionInvocation(action);
        Period snoozePeriod = minutes(2);
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

    static void runningAlarms(final UpnpService upnpService, final Device device) {
        final Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
        Action action = avTransportService.getAction("GetRunningAlarmProperties");
        final ActionInvocation invocation = new ActionInvocation(action);
        new ActionCallback.Default(invocation, upnpService.getControlPoint()).run();
        ActionException anException = invocation.getFailure();
        System.out.println("invocation = " + invocation.getOutputMap());
        if (anException != null && anException.getMessage() != null) {
            System.out.println("anException.getMessage() = " + anException.getMessage());
        }
    }

    static void stop(final UpnpService upnpService, final Device device) {
        final Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
        Action action = avTransportService.getAction("Stop");
        final ActionInvocation invocation = new ActionInvocation(action);
        new ActionCallback.Default(invocation, upnpService.getControlPoint()).run();
        ActionException anException = invocation.getFailure();
        if (anException != null && anException.getMessage() != null) {
            System.out.println("anException.getMessage() = " + anException.getMessage());
        }
    }

    static void sleep(final UpnpService upnpService, final UnexpectedEventsListener unexpectedEventsListener, final Device device) {
        final Service avTransportService = device.findService(new UDAServiceId("AVTransport"));
        Action playAction = avTransportService.getAction("Play");
        final ActionInvocation playInvocation = new ActionInvocation(playAction);
        playInvocation.setInput("Speed", "1");
        new ActionCallback.Default(playInvocation, upnpService.getControlPoint()).run();
        ActionException playException = playInvocation.getFailure();
        if (playException != null && playException.getMessage() != null) {
            System.out.println("anException.getMessage() = " + playException.getMessage());
        }

        Action action = avTransportService.getAction("ConfigureSleepTimer");
        final ActionInvocation invocation = new ActionInvocation(action);
        Period snoozePeriod = minutes(2);
        PeriodFormatter pFormatter = new PeriodFormatterBuilder()
                .printZeroAlways()
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();

        try {
            invocation.setInput("NewSleepTimerDuration", pFormatter.print(snoozePeriod));
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
