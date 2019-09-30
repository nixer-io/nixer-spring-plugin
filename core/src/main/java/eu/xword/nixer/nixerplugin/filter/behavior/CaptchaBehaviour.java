package eu.xword.nixer.nixerplugin.filter.behavior;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaBehaviour implements Behavior {

    public static final String CAPTCHA = "captcha";

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        request.setAttribute("nixer.captcha.enabled", true);
    }

    @Override
    public Category category() {
        return Category.STACKABLE;
    }

    @Override
    public String name() {
        return CAPTCHA;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
