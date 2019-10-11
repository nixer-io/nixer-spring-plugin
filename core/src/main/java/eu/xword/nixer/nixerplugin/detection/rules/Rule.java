package eu.xword.nixer.nixerplugin.detection.rules;

import eu.xword.nixer.nixerplugin.login.LoginContext;

/**
 * Abstraction for detecting anomalies in login activity
 */
public interface Rule {

    void execute(final LoginContext loginContext, final EventEmitter eventEmitter);

}
