package net.sourceforge.jsonos;

public interface AlarmsListener {
    void gotAlarms(Alarms alarms);

    void failedToGetAlarms(String detailsMessage);
}
