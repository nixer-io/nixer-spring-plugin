package eu.xword.nixer.nixerplugin.stigma;

import eu.xword.nixer.nixerplugin.LoginContext;
import eu.xword.nixer.nixerplugin.LoginResult;

public interface StigmaService {
    StigmaToken refreshStigma(StigmaToken receivedStigma, LoginResult loginResult, final LoginContext loginContext);
}
