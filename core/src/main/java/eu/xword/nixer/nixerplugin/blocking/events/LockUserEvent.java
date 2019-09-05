package eu.xword.nixer.nixerplugin.blocking.events;

import eu.xword.nixer.nixerplugin.blocking.EventVisitor;

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

    @Override
    public String type() {
        return "LOCK_USER";
    }

    @Override
    public void accept(final EventVisitor visitor) {
        visitor.accept(this);
    }
}
