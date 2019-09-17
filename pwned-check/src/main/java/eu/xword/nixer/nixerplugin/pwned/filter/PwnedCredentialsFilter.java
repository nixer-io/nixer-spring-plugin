package eu.xword.nixer.nixerplugin.pwned.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.filter.NixerFilter;

/**
 * Created on 17/09/2019.
 *
 * @author gcwiak
 */
public class PwnedCredentialsFilter extends NixerFilter {
    @Override
    protected boolean applies(final HttpServletRequest request) throws IOException {
        return false;
    }
}
