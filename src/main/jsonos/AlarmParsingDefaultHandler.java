package jsonos;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;
import java.util.List;

import static jsonos.Alarm.disabledAlarm;
import static jsonos.Alarm.enabledAlarm;

final class AlarmParsingDefaultHandler extends DefaultHandler {
    private static final DateTimeFormatter TIME_FORMATTER = ISODateTimeFormat.localTimeParser();

    private final List<Alarm> alarms = new LinkedList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("Alarm".equals(qName)) {
            final int startTimeIndex = attributes.getIndex("StartTime");
            final String startTimeString = attributes.getValue(startTimeIndex);
            final LocalTime startTime = TIME_FORMATTER.parseLocalTime(startTimeString); // TODO - what timezone is this in?
            final int enabledIndex = attributes.getIndex("Enabled");
            final String enabledString = attributes.getValue(enabledIndex);
            alarms.add("1".equals(enabledString) ? enabledAlarm(startTime) : disabledAlarm(startTime));
        }
    }

    public Alarms alarms() {
        return new Alarms(alarms);
    }

}
