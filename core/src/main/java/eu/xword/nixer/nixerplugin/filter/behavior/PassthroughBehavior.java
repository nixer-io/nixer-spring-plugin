package eu.xword.nixer.nixerplugin.filter.behavior;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassthroughBehavior implements Behavior {

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        // do nothing
    }
}
