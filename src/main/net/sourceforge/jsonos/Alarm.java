package net.sourceforge.jsonos;

import org.joda.time.LocalTime;

public abstract class Alarm {

    public final int id;

    private Alarm(final int id) {
        this.id = id;
    }

    public static Alarm enabledAlarm(final LocalTime startTime, final int id) {
        return new Alarm(id) {
            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public String toString() {
                return "EnabledAlarm{" +
                        "startTime=" + startTime +
                        ", id=" + id +
                        '}';
            }

        };

    }

    public static Alarm disabledAlarm(final LocalTime startTime, final int id) {
        return new Alarm(id) {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public String toString() {
                return "DisabledAlarm{" +
                        "startTime=" + startTime +
                        ", id=" + id +
                        '}';
            }
        };
    }

    public abstract boolean isEnabled();

}
