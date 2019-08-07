package eu.xword.nixer.nixerplugin.stigma;

import eu.xword.nixer.nixerplugin.login.LoginContext;
import eu.xword.nixer.nixerplugin.login.LoginResult;

public interface StigmaService {
    StigmaToken refreshStigma(StigmaToken receivedStigma, LoginResult loginResult, final LoginContext loginContext);
}
