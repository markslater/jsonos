package jsonos;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;

final class ListAlarmsActionCallback extends ActionCallback {

    private final AlarmDetailsListener alarmDetailsListener;

    public ListAlarmsActionCallback(AlarmDetailsListener alarmDetailsListener, ActionInvocation getStatusInvocation) {
        super(getStatusInvocation);
        this.alarmDetailsListener = alarmDetailsListener;
    }

    @Override
    public void success(ActionInvocation invocation) {
        alarmDetailsListener.gotDetails(invocation.getOutput("CurrentAlarmList").toString());
    }

    @Override
    public void failure(ActionInvocation invocation,
                        UpnpResponse operation,
                        String defaultMsg) {
        alarmDetailsListener.failedToGetDetails(defaultMsg);
    }
}
