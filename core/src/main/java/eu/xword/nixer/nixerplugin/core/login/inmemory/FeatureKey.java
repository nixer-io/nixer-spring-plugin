package eu.xword.nixer.nixerplugin.core.login.inmemory;

import eu.xword.nixer.nixerplugin.core.login.LoginContext;

/**
 * Extract feature values from {@link LoginContext}.
 */
public interface FeatureKey {

    String key(LoginContext loginContext);

    /**
     * Predefined features keys
     */
    enum Features implements FeatureKey {
        IP {
            @Override
            public String key(LoginContext loginContext) {
                return loginContext.getIpAddress();
            }
        },
        USERNAME {
            @Override
            public String key(final LoginContext loginContext) {
                return loginContext.getUsername();
            }
        },
        USER_AGENT_TOKEN {
            @Override
            public String key(final LoginContext loginContext) {
                return loginContext.getUserAgentToken();
            }
        };

        @Override
        public abstract String key(LoginContext loginContext);
    }
}
