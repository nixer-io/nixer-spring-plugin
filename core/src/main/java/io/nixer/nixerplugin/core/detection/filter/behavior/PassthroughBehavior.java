package io.nixer.nixerplugin.core.detection.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.nixer.nixerplugin.core.detection.filter.behavior.Behaviors.PASSTHROUGH;

/**
 * This behavior just ignores request. Kind of NullObject.
 */
public class PassthroughBehavior implements Behavior {

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        // do nothing
    }

    @Override
    public boolean isCommitting() {
        return false;
    }

    @Override
    public String name() {
        return PASSTHROUGH.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
