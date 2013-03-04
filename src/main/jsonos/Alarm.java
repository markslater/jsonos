package jsonos;

import org.joda.time.LocalTime;

public abstract class Alarm {

    public static Alarm enabledAlarm(final LocalTime startTime) {
        return new Alarm() {
            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public String toString() {
                return "EnabledAlarm{" +
                        "startTime=" + startTime +
                        '}';
            }

        };

    }

    public static Alarm disabledAlarm(final LocalTime startTime) {
        return new Alarm() {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public String toString() {
                return "DisabledAlarm{" +
                        "startTime=" + startTime +
                        '}';
            }
        };
    }

    private Alarm() {
    }

    public abstract boolean isEnabled();

}
