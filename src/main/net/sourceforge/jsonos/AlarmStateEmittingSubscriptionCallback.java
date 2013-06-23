package net.sourceforge.jsonos;

import com.google.common.base.Optional;
import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.gena.RemoteGENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.state.StateVariableValue;

final class AlarmStateEmittingSubscriptionCallback extends SubscriptionCallback {
    private final UnexpectedEventsListener unexpectedEventsListener;

    public AlarmStateEmittingSubscriptionCallback(Service service, final UnexpectedEventsListener unexpectedEventsListener) {
        super(service);
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
        final String lastChange = (String) ((StateVariableValue) (((RemoteGENASubscription) subscription).getCurrentValues().get("LastChange"))).getValue();
        System.out.println("new AvTransportStateVariables(Optional.<Boolean>absent()).parse(lastChange).isAlarmRunning() = " + new AvTransportStateVariables(Optional.<Boolean>absent()).parse(lastChange).isAlarmRunning());
    }

    @Override
    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        unexpectedEventsListener.eventsMissed(subscription, numberOfMissedEvents);
    }

}
