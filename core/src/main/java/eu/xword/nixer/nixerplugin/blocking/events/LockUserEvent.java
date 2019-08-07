package eu.xword.nixer.nixerplugin.blocking.events;

public class LockUserEvent extends BlockEvent {
    /**
     * Create a new ApplicationEvent.
     * @param username the object on which the event initially occurred (never {@code null})
     */
    public LockUserEvent(final String username) {
        super(username);
    }

    public String getUsername() {
        return (String) source;
    }

    @Override
    public String toString() {
        return "LockUserEvent username:" + getUsername();
    }
}
