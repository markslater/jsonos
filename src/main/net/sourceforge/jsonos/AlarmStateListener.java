package net.sourceforge.jsonos;

interface AlarmStateListener {
    void gotStateChange(String stateChange);

    void failedToGetStateChange(String detailsMessage);
}
