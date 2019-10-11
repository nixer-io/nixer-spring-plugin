package eu.xword.nixer.nixerplugin.events;

import org.springframework.context.ApplicationEvent;

public abstract class BlockEvent extends ApplicationEvent {

    public BlockEvent(final Object source) {
        super(source);
    }

    public abstract String type();

    public abstract void accept(EventVisitor visitor);
}
