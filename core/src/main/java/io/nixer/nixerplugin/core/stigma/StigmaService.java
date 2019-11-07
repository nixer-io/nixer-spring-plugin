package io.nixer.nixerplugin.core.stigma;

import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;

public interface StigmaService {
    StigmaToken refreshStigma(StigmaToken receivedStigma, LoginResult loginResult, final LoginContext loginContext);
}
