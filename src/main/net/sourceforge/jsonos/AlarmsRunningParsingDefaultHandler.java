package net.sourceforge.jsonos;

import com.google.common.base.Optional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class AlarmsRunningParsingDefaultHandler extends DefaultHandler {

    private Optional<Boolean> alarmRunning;

    public AlarmsRunningParsingDefaultHandler(final Optional<Boolean> alarmRunning) {
        this.alarmRunning = alarmRunning;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("AlarmRunning".equals(localName) && "urn:schemas-rinconnetworks-com:metadata-1-0/".equals(uri)) {
            final int valIndex = attributes.getIndex("val");
            final String val = attributes.getValue(valIndex);
            alarmRunning = Optional.of("1".equals(val));
        }
    }

    public AvTransportStateVariables avTransportStateVariables() {
        return new AvTransportStateVariables(alarmRunning);
    }

}
