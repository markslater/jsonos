package jsonos;

import org.junit.Test;

public class ConnectionTest {

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {
        try (final SonosClient sonosClient = new SonosClient(new SysOutAlarmsListener())) {
            Thread.sleep(180000);
        }
    }

}
