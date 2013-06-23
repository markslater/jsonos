package net.sourceforge.jsonos;

import java.util.Collections;

public final class RememberingAlarmsListener implements AlarmsListener {

    private final Object lock = new Object();
    private Alarms alarms = new Alarms(Collections.<Alarm>emptyList());

    @Override
    public void gotAlarms(final Alarms alarms) {
        synchronized (lock) {
            this.alarms = alarms;
        }
    }

    @Override
    public void failedToGetAlarms(final String detailsMessage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Alarms alarms() {
        return alarms;
    }
}
