package net.sourceforge.jsonos;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class PeriodicalAlarmOutputter {
    public static void main(String[] args) throws Exception {
        final RememberingAlarmsListener alarmsListener = new RememberingAlarmsListener();
        final SonosClient sonosClient = new SonosClient(alarmsListener, new SysErrUnexpectedEventsListener());
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("alarmsListener.alarms() = " + alarmsListener.alarms());
                    }
                }, 1, 1, TimeUnit.MINUTES
        );
    }

    private static final class RememberingAlarmsListener implements AlarmsListener {

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

}
