package net.sourceforge.jsonos;

import org.junit.Test;

public class SonosClientTest {

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {
        final SonosClient sonosClient = new SonosClient(new SysOutAlarmsListener(), new SysErrUnexpectedEventsListener());
        Thread.sleep(10000);
    }

}
