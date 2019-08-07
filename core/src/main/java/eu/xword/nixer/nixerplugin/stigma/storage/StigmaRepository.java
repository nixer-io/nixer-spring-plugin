package eu.xword.nixer.nixerplugin.stigma.storage;

import eu.xword.nixer.nixerplugin.login.LoginResult;

public interface StigmaRepository {
    void save(final String stigma, final LoginResult loginResult);
}
