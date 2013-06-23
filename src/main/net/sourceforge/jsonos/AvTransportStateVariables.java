package net.sourceforge.jsonos;

import com.google.common.base.Optional;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

final class AvTransportStateVariables {
    private final Optional<Boolean> alarmRunning;

    public AvTransportStateVariables(final Optional<Boolean> isAlarmRunning) {
        alarmRunning = isAlarmRunning;
    }

    static AvTransportStateVariables parseInitialState(String stateVariablesXml) {
        return new AvTransportStateVariables(Optional.<Boolean>absent()).parse(stateVariablesXml);
    }

    AvTransportStateVariables parse(String stateVariablesXml) {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  // TODO - make pool?  This isn't threadsafe, but might be expensive
        saxParserFactory.setNamespaceAware(true);
        final AlarmsRunningParsingDefaultHandler alarmParsingDefaultHandler = new AlarmsRunningParsingDefaultHandler(alarmRunning);
        try {
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new InputSource(new StringReader(stateVariablesXml)), alarmParsingDefaultHandler);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return alarmParsingDefaultHandler.avTransportStateVariables();
    }

    public boolean isAlarmRunning() {
        return alarmRunning.or(false);
    }
}
