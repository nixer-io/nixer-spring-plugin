package eu.xword.nixer.nixerplugin;

public class EventLogConfiguration {

    private boolean enabled = true;

    private Target target = Target.LOG;

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(final Target target) {
        this.target = target;
    }

    public enum Target {
        LOG
    }
}
