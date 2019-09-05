package eu.xword.nixer.nixerplugin.blocking.events;

import eu.xword.nixer.nixerplugin.blocking.EventVisitor;
import org.springframework.context.ApplicationEvent;

public abstract class BlockEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BlockEvent(final Object source) {
        super(source);
    }

    public abstract String type();

    public abstract void accept(EventVisitor visitor);
}
