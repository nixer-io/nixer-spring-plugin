package eu.xword.nixer.nixerplugin.rules;

import eu.xword.nixer.nixerplugin.login.LoginContext;

public interface Rule {
    void execute(final LoginContext loginContext, final EventEmitter eventEmitter);
}
