package net.sourceforge.jsonos;

import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.state.StateVariableValue;
import org.teleal.cling.model.types.InvalidValueException;

import java.util.Arrays;
import java.util.Map;

public final class SysErrUnexpectedEventsListener implements UnexpectedEventsListener {
    @Override
    public void subscriptionEnded(final GENASubscription subscription, final CancelReason reason, final UpnpResponse responseStatus) {
        System.err.println("unexpected subscription ended{subscription{" + subscription + "}, reason{" + reason + "}, responseStatus{" + responseStatus + "}}");
    }

    @Override
    public void subscriptionFailed(final GENASubscription subscription, final UpnpResponse responseStatus, final Exception exception, final String defaultMsg) {
        System.err.println("unexpected subscription failed{subscription{" + subscription + "}, responseStatus{" + responseStatus + "}, exception{ " + exception + "}, defaultMsg{" + defaultMsg + "}}");
    }

    @Override
    public void noAlarmListVersionInEvent(final GENASubscription subscription, final Map<String, StateVariableValue> values) {
        System.err.println("unexpected no alarm list version in event{subscription{" + subscription + "}, values{" + values + "}}");
    }

    @Override
    public void eventsMissed(final GENASubscription subscription, final int numberOfMissedEvents) {
        System.err.println("unexpected events missed{subscription{" + subscription + "}, numberOfMissedEvents{" + numberOfMissedEvents + "}}");
    }

    @Override
    public void unexpectedDeviceCallback(final RemoteDevice device) {
        System.err.println("unexpected device callback from " + device.getType() + " with services " + Arrays.toString(device.getServices()));
    }

    @Override
    public void invalidValueSettingSnoozePeriod(final InvalidValueException e) {
        System.err.println("unexpected invalid value setting snooze period " + e);
    }

}
