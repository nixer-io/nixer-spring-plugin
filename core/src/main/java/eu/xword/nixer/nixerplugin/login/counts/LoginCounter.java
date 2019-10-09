package eu.xword.nixer.nixerplugin.login.counts;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;

public interface LoginCounter {

    void onLogin(final LoginResult result, final LoginContext context);
}
