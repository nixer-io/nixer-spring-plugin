package eu.xword.nixer.nixerplugin.blocking.events;

import eu.xword.nixer.nixerplugin.blocking.EventVisitor;

public class GlobalCredentialStuffingEvent extends BlockEvent {
    /**
     * Create a new ApplicationEvent.
     */
    public GlobalCredentialStuffingEvent() {
        super("");
    }

    public void accept(EventVisitor visitor) {
        visitor.accept(this);
    }

    @Override
    public String type() {
        return "GLOBAL_CREDENTIAL_STUFFING";
    }
}
