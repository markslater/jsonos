package jsonos;

public interface AlarmDetailsListener {
    void gotDetails(String details);

    void failedToGetDetails(String detailsMessage);
}
