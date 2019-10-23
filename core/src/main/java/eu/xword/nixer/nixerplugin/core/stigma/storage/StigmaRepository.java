package eu.xword.nixer.nixerplugin.core.stigma.storage;

import eu.xword.nixer.nixerplugin.core.login.LoginResult;

public interface StigmaRepository {
    void save(final String stigma, final LoginResult loginResult);
}
