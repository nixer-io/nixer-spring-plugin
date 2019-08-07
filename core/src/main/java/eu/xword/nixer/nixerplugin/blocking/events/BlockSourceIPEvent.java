package eu.xword.nixer.nixerplugin.blocking.events;

public class BlockSourceIPEvent extends BlockEvent {
    /**
     * Create a new ApplicationEvent.
     * @param ip the object on which the event initially occurred (never {@code null})
     */
    public BlockSourceIPEvent(final String ip) {
        super(ip);
    }

    public String getIp() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "BlockSourceIpEvent ip:" + getIp();
    }
}
