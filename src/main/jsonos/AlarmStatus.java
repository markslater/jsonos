package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.UDAServiceId;

import java.util.ArrayList;
import java.util.Map;

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
            upnpService.getControlPoint().execute(new SubscriptionCallback(service) {
                @Override
                protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
                    System.err.println("Failed: " + defaultMsg);
                }

                @Override
                protected void established(GENASubscription subscription) {
                    System.out.println("Established: " + subscription.getSubscriptionId());
                }

                @Override
                protected void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                protected void eventReceived(GENASubscription subscription) {
                    System.out.println("Event: " + subscription.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = subscription.getCurrentValues();
                    if (values.containsKey("AlarmListVersion")) {
                        System.out.println("values.get(\"AlarmListVersion\") = " + values.get("AlarmListVersion"));
                        Action listAlarmsAction = service.getAction("ListAlarms");
                        ActionInvocation getStatusInvocation = new ActionInvocation(listAlarmsAction);
                        upnpService.getControlPoint().execute(new ActionCallback(getStatusInvocation) {

                            @Override
                            public void success(ActionInvocation invocation) {
                                final String currentAlarmList = invocation.getOutput("CurrentAlarmList").toString();
                                System.out.println("currentAlarmList = " + currentAlarmList);
                                alarmDetailsListener.gotDetails(currentAlarmList);
                            }

                            @Override
                            public void failure(ActionInvocation invocation,
                                                UpnpResponse operation,
                                                String defaultMsg) {
                                alarmDetailsListener.failedToGetDetails(defaultMsg);
                            }
                        });

                    } else {
                        System.out.println("No AlarmListVersion in event");
                    }
                }

                @Override
                protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                    System.out.println("Missed events: " + numberOfMissedEvents);
                }
            });
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
