package jsonos;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Alarms implements Iterable<Alarm> {

    private final List<Alarm> alarms;

    public Alarms(List<Alarm> alarms) {
        this.alarms = new ArrayList<>(alarms);
    }

    public static Alarms parse(String alarmResults) {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  // TODO - make pool?  This isn't threadsafe, but might be expensive
        final AlarmParsingDefaultHandler alarmParsingDefaultHandler = new AlarmParsingDefaultHandler();
        try {
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new InputSource(new StringReader(alarmResults)), alarmParsingDefaultHandler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return alarmParsingDefaultHandler.alarms();
    }

    @Override
    public Iterator<Alarm> iterator() {
        return alarms.iterator();
    }

    @Override
    public String toString() {
        return "Alarms{" +
                "alarms=" + alarms +
                '}';
    }
}
