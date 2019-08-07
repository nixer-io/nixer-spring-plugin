package eu.xword.nixer.nixerplugin;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    public static String extractUsername(final HttpServletRequest httpServletRequest) {
        final Principal principal = httpServletRequest.getUserPrincipal();

        return principal != null
                ? principal.getName()
                : null;
    }
}
