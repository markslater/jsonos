package net.sourceforge.jsonos;

import java.util.Collections;

final class SysOutAlarmsListener implements AlarmsListener {

    public Alarms alarms = new Alarms(Collections.<Alarm>emptyList());

    @Override
    public void gotAlarms(final Alarms alarms) {
        this.alarms = alarms;
        System.out.println("alarms = " + alarms);
    }

    @Override
    public void failedToGetAlarms(final String detailsMessage) {
        System.out.println("detailsMessage = " + detailsMessage);
    }
}
