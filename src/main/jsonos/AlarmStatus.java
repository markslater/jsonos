package jsonos;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.Device;

public interface AlarmStatus {
    AlarmStatus deviceAdded(final Device device, UpnpService upnpService, AlarmDetailsListener alarmDetailsListener);

}
