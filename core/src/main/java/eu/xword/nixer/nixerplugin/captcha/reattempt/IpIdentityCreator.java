package eu.xword.nixer.nixerplugin.captcha.reattempt;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

public class IpIdentityCreator implements IdentityCreator {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public String key() {
        final String remoteAddr = httpServletRequest.getRemoteAddr();
//        final String username = UserUtils.extractUsername(httpServletRequest);
//        FIXME UserPrincipal is not set in SecurityContextHolder need to find other way to access username

        final String username = "user";

        return remoteAddr + ":" + username;
    }
}
