package eu.xword.nixer.nixerplugin.filter.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassthroughBehavior implements MitigationStrategy {

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        // do nothing
    }
}
