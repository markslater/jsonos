package net.sourceforge.jsonos;

import org.hamcrest.Matchers;
import org.joda.time.LocalTime;
import org.junit.Test;

import static net.sourceforge.jsonos.Alarm.enabledAlarm;
import static net.sourceforge.jsonos.MoreMatchers.equalsReflectively;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public final class AlarmsTest {
    @Test
    public void canParseAnEmptyAlarmResult() throws Exception {
        final String alarmResults = "<Alarms></Alarms>";
        assertThat(Alarms.parse(alarmResults), Matchers.<Alarm>emptyIterable());
    }

    @Test
    public void canParseASingleAlarm() throws Exception {
        final String alarmResults = "<Alarms><Alarm ID=\"15\" StartTime=\"07:00:00\" Duration=\"02:00:00\" Recurrence=\"DAILY\" Enabled=\"1\" RoomUUID=\"RINCON_000E5833DDC201400\" ProgramURI=\"x-rincon-buzzer:0\" ProgramMetaData=\"\" PlayMode=\"SHUFFLE_NOREPEAT\" Volume=\"25\" IncludeLinkedZones=\"0\"/></Alarms>";
        assertThat(Alarms.parse(alarmResults), contains(equalsReflectively(enabledAlarm(new LocalTime(7, 0, 0)))));
    }
}
