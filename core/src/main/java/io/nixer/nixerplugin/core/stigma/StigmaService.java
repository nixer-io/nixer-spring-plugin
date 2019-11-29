package io.nixer.nixerplugin.core.stigma;

import io.nixer.nixerplugin.core.login.LoginResult;

public interface StigmaService {
    StigmaToken refreshStigma(StigmaToken receivedStigma, LoginResult loginResult);
}
