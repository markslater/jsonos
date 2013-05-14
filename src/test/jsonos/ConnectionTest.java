package jsonos;

import org.junit.Test;

public class ConnectionTest {

    @Test
    public void findsZonePlayersWithAlarmRegistryListener() throws Exception {
        System.out.println("Starting Cling...");
        try (final SonosClient sonosClient = new SonosClient(new SysOutAlarmsListener())) {
            Thread.sleep(10000);
        }
        System.out.println("Stopping Cling...");
    }

}
