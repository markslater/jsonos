package jsonos;

final class SysOutAlarmDetailsListener implements AlarmDetailsListener {
    @Override
    public void gotDetails(String details) {
        final Alarms alarms = Alarms.parse(details);
        System.out.println("alarms = " + alarms);
    }

    @Override
    public void failedToGetDetails(String detailsMessage) {
        System.out.println("detailsMessage = " + detailsMessage);
    }
}
