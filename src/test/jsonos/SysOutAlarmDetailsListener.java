package jsonos;

final class SysOutAlarmDetailsListener implements AlarmDetailsListener {
    @Override
    public void gotDetails(String details) {
        System.out.println("details = " + details);
    }

    @Override
    public void failedToGetDetails(String detailsMessage) {
        System.out.println("detailsMessage = " + detailsMessage);
    }
}
