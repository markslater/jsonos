package net.sourceforge.jsonos;

import com.google.common.base.Optional;

class ParsingAlarmStateListener implements AlarmStateListener {
    private final SonosStateListener sonosStateListener;

    public ParsingAlarmStateListener(final SonosStateListener sonosStateListener) {
        this.sonosStateListener = sonosStateListener;
    }

    @Override
    public void gotStateChange(final String stateChange) {
        System.out.println("lastChange = " + stateChange);
        sonosStateListener.gotState(new AvTransportStateVariables(Optional.<Boolean>absent()).parse(stateChange).isAlarmRunning());
    }

    @Override
    public void failedToGetStateChange(final String detailsMessage) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
