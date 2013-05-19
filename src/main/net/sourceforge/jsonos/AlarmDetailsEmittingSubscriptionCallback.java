package net.sourceforge.jsonos;

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
    private final UnexpectedEventsListener unexpectedEventsListener;

    public AlarmDetailsEmittingSubscriptionCallback(Service service, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener, final UnexpectedEventsListener unexpectedEventsListener) {
        super(service);
        this.upnpService = upnpService;
        this.alarmDetailsListener = alarmDetailsListener;
        this.unexpectedEventsListener = unexpectedEventsListener;
    }

    @Override
    protected void failed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg) {
        unexpectedEventsListener.subscriptionFailed(subscription, responseStatus, exception, defaultMsg);
    }

    @Override
    protected void established(GENASubscription subscription) {
        // All good - hope to end up here.
    }

    @Override
    protected void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
        unexpectedEventsListener.subscriptionEnded(subscription, reason, responseStatus);
    }

    @Override
    protected void eventReceived(GENASubscription subscription) {
        Map<String, StateVariableValue> values = subscription.getCurrentValues();
        if (values.containsKey("AlarmListVersion")) {
            upnpService.getControlPoint().execute(new ListAlarmsActionCallback(alarmDetailsListener, new ActionInvocation(service.getAction("ListAlarms"))));
        } else {
            unexpectedEventsListener.noAlarmListVersionInEvent(subscription, values);
        }
    }

    @Override
    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        unexpectedEventsListener.eventsMissed(subscription, numberOfMissedEvents);
    }

}
