package jsonos;

final class ParsingAlarmDetailsListener implements AlarmDetailsListener {

    private final AlarmsListener alarmsListener;

    ParsingAlarmDetailsListener(final AlarmsListener alarmsListener) {
        this.alarmsListener = alarmsListener;
    }

    @Override
    public void gotDetails(String details) {
        alarmsListener.gotAlarms(Alarms.parse(details));
    }

    @Override
    public void failedToGetDetails(String detailsMessage) {
        alarmsListener.failedToGetAlarms(detailsMessage);
    }
}
