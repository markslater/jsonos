package net.sourceforge.jsonos;

public interface SonosStateListener {
    void gotState(final boolean alarmRunning);

    void failedToGetState(String detailsMessage);
}
