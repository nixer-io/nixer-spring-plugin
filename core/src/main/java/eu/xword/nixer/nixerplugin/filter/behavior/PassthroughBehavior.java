package eu.xword.nixer.nixerplugin.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassthroughBehavior implements Behavior {

    public static final String PASSTHROUGH = "passthrough";

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
        return PASSTHROUGH;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
