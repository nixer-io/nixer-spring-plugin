package eu.xword.nixer.nixerplugin.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static eu.xword.nixer.nixerplugin.filter.behavior.Behaviors.PASSTHROUGH;

/**
 * This behavior just ignores request. Kind of NullObject.
 */
public class PassthroughBehavior implements Behavior {

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        // do nothing
    }

    @Override
    public Category category() {
        return Category.EXCLUSIVE;
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
