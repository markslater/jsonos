package net.sourceforge.jsonos;

import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.InvalidValueException;

import java.util.Map;

interface UnexpectedEventsListener {
    void subscriptionEnded(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus);

    void subscriptionFailed(GENASubscription subscription, UpnpResponse responseStatus, Exception exception, String defaultMsg);

    void noAlarmListVersionInEvent(GENASubscription subscription, Map<String, StateVariableValue> values);

    void eventsMissed(GENASubscription subscription, int numberOfMissedEvents);

    void unexpectedDeviceCallback(RemoteDevice device);

    void invalidValueSettingSnoozePeriod(InvalidValueException e);
}
