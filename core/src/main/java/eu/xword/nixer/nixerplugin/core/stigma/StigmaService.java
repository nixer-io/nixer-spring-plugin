package eu.xword.nixer.nixerplugin.core.stigma;

import eu.xword.nixer.nixerplugin.core.login.LoginContext;
import eu.xword.nixer.nixerplugin.core.login.LoginResult;

public interface StigmaService {
    StigmaToken refreshStigma(StigmaToken receivedStigma, LoginResult loginResult, final LoginContext loginContext);
}
