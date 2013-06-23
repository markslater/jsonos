package net.sourceforge.jsonos;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class AvTransportStateVariablesTest {
    @Test
    public void parsesSampleAvTransportStateVariablesXml() throws Exception {
        assertThat(AvTransportStateVariables.parseInitialState("<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\" xmlns:r=\"urn:schemas-rinconnetworks-com:metadata-1-0/\">\n" +
                "<InstanceID val=\"0\">\n" +
                "<TransportState val=\"PLAYING\"/>\n" +
                "<CurrentPlayMode val=\"NORMAL\"/>\n" +
                "<CurrentCrossfadeMode val=\"0\"/>\n" +
                "<NumberOfTracks val=\"1\"/>\n" +
                "<CurrentTrack val=\"1\"/>\n" +
                "<CurrentSection val=\"0\"/>\n" +
                "<CurrentTrackURI val=\"x-rincon-buzzer:0\"/>\n" +
                "<CurrentTrackDuration val=\"0:00:00\"/>\n" +
                "<CurrentTrackMetaData val=\"&lt;DIDL-Lite xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:upnp=&quot;urn:schemas-upnp-org:metadata-1-0/upnp/&quot; xmlns:r=&quot;urn:schemas-rinconnetworks-com:metadata-1-0/&quot; xmlns=&quot;urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/&quot;&gt;&lt;item id=&quot;-1&quot; parentID=&quot;-1&quot; restricted=&quot;true&quot;&gt;&lt;res protocolInfo=&quot;x-rincon-buzzer:*:*:*&quot;&gt;x-rincon-buzzer:0&lt;/res&gt;&lt;r:streamContent&gt;&lt;/r:streamContent&gt;&lt;r:radioShowMd&gt;&lt;/r:radioShowMd&gt;&lt;dc:title&gt;x-rincon-buzzer:0&lt;/dc:title&gt;&lt;upnp:class&gt;object.item&lt;/upnp:class&gt;&lt;/item&gt;&lt;/DIDL-Lite&gt;\"/>\n" +
                "<r:NextTrackURI val=\"\"/>\n" +
                "<r:NextTrackMetaData val=\"\"/>\n" +
                "<r:EnqueuedTransportURI val=\"x-rincon-buzzer:0\"/>\n" +
                "<r:EnqueuedTransportURIMetaData val=\"\"/>\n" +
                "<PlaybackStorageMedium val=\"NETWORK\"/>\n" +
                "<AVTransportURI val=\"x-rincon-buzzer:0\"/>\n" +
                "<AVTransportURIMetaData val=\"\"/>\n" +
                "<CurrentTransportActions val=\"Set, Play, Stop, Pause, Seek, Next, Previous\"/>\n" +
                "<r:AlarmRunning val=\"1\"/>\n" +
                "</InstanceID>\n" +
                "</Event>").isAlarmRunning(), equalTo(true));
    }
}
