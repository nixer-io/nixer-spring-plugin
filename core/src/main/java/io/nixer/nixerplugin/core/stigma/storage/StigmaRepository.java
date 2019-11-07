package io.nixer.nixerplugin.core.stigma.storage;

import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.login.LoginResult;

public interface StigmaRepository {
    void save(final String stigma, final LoginResult loginResult);
}
