package io.nixer.nixerplugin.core.login.inmemory;

import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;

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
        },
        LOGIN_STATUS {
            @Override
            public String key(final LoginContext loginContext) {
                if (loginContext.getLoginResult() == null) {
                    return null;
                }
                if (loginContext.getLoginResult().isSuccess()) {
                    return LoginResult.Status.SUCCESS.name();
                } else {
                    return LoginResult.Status.FAILURE.name();
                }
            }
        };

        @Override
        @Nullable
        public abstract String key(LoginContext loginContext);
    }
}
