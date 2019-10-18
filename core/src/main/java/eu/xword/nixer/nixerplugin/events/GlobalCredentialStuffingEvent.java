package eu.xword.nixer.nixerplugin.events;

/**
 * This events is emitted when system is under credential stuffing attack.
 */
public class GlobalCredentialStuffingEvent extends AnomalyEvent {

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
