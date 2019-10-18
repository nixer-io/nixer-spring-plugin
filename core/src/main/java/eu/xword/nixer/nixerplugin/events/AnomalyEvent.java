package eu.xword.nixer.nixerplugin.events;

import org.springframework.context.ApplicationEvent;


/**
 * Represents event detected by anomaly rule
 */
public abstract class AnomalyEvent extends ApplicationEvent {

    public AnomalyEvent(final Object source) {
        super(source);
    }

    public abstract String type();

    public abstract void accept(EventVisitor visitor);
}
