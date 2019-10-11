package eu.xword.nixer.nixerplugin.events;

import org.springframework.context.ApplicationEvent;

public abstract class DetectionEvent extends ApplicationEvent {

    public DetectionEvent(final Object source) {
        super(source);
    }

    public abstract String type();

    public abstract void accept(EventVisitor visitor);
}
