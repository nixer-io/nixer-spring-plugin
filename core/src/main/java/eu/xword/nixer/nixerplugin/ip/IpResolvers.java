package eu.xword.nixer.nixerplugin.ip;

import javax.servlet.http.HttpServletRequest;

public enum IpResolvers implements IpResolver {

    REMOTE_ADDRESS {
        @Override
        public String resolve(final HttpServletRequest request) {
            return request.getRemoteAddr();
        }
    },
    X_FORWARDED_FOR {
        @Override
        public String resolve(final HttpServletRequest request) {
            //TODO handle x-forwarded-for and x-forwarded
            throw new UnsupportedOperationException();
        }
    };

    @Override
    public abstract String resolve(final HttpServletRequest request);
}
