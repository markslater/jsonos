package net.sourceforge.jsonos;

public interface Stoppable extends AutoCloseable {
    @Override
    public void close();
}
