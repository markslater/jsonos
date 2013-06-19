package net.sourceforge.jsonos;

import org.junit.Test;

public class SonosClientTest {

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {
        final SysOutAlarmsListener alarmsListener = new SysOutAlarmsListener();
        final SonosClient sonosClient = new SonosClient(alarmsListener, new SysErrUnexpectedEventsListener());
        Thread.sleep(10000);
        sonosClient.snooze();
        Thread.sleep(10000);
        sonosClient.close();
    }

}
