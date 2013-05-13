package jsonos;

final class SysOutAlarmsListener implements AlarmsListener {
    @Override
    public void gotAlarms(final Alarms alarms) {
        System.out.println("alarms = " + alarms);
    }

    @Override
    public void failedToGetAlarms(final String detailsMessage) {
        System.out.println("detailsMessage = " + detailsMessage);
    }
}
