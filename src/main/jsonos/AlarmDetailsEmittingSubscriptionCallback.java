package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.state.StateVariableValue;

import java.util.Map;

final class AlarmDetailsEmittingSubscriptionCallback extends SubscriptionCallback {
    private final UpnpService upnpService;
    private final AlarmDetailsListener alarmDetailsListener;

    public AlarmDetailsEmittingSubscriptionCallback(Service service, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener) {
        super(service);
        this.upnpService = upnpService;
        this.alarmDetailsListener = alarmDetailsListener;
    }

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
            ActionInvocation getStatusInvocation = new ActionInvocation(service.getAction("ListAlarms"));
            upnpService.getControlPoint().execute(new ListAlarmsActionCallback(alarmDetailsListener, getStatusInvocation));

        } else {
            System.out.println("No AlarmListVersion in event");
        }
    }

    @Override
    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        System.out.println("Missed events: " + numberOfMissedEvents);
    }

}
