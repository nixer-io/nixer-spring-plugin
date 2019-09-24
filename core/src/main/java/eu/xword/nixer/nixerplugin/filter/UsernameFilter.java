package eu.xword.nixer.nixerplugin.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.UserUtils;
import eu.xword.nixer.nixerplugin.registry.BlockedUserRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static eu.xword.nixer.nixerplugin.filter.RequestAugmentation.USERNAME_BLOCKED;

//@Component
public class UsernameFilter extends MetadataFilter {

    @Autowired
    private BlockedUserRegistry blockedUserRegistry;

    @Override
    protected void apply(final HttpServletRequest request) {
        final String username = UserUtils.extractUsername(request);
        if (blockedUserRegistry.isBlocked(username)) {
            request.setAttribute(USERNAME_BLOCKED, true);
        }
    }

}
