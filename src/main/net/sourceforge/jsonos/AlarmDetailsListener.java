package net.sourceforge.jsonos;

interface AlarmDetailsListener {
    void gotDetails(String details);

    void failedToGetDetails(String detailsMessage);
}
