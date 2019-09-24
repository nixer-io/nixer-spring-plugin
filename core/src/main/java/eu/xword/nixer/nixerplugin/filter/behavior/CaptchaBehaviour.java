package eu.xword.nixer.nixerplugin.filter.behavior;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaBehaviour implements Behavior {
    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        request.setAttribute("nixer.captcha.enabled", true);
    }

    @Override
    public Category category() {
        return Category.STACKABLE;
    }
}
