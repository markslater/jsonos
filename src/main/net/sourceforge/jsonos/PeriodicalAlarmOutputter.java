package net.sourceforge.jsonos;

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

}
