package io.nixer.nixerplugin.core.detection.rules;

import io.nixer.nixerplugin.core.login.LoginContext;

/**
 * Abstraction for detecting anomalies in login activity
 */
public interface LoginRule {

    void execute(final LoginContext loginContext, final EventEmitter eventEmitter);

}
