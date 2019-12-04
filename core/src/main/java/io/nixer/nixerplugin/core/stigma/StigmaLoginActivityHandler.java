package io.nixer.nixerplugin.core.stigma;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.core.login.LoginActivityHandler;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;

/**
 * Created on 28/11/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaLoginActivityHandler implements LoginActivityHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final StigmaCookieService stigmaCookieService;
    private final StigmaService stigmaService;

    public StigmaLoginActivityHandler(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      final StigmaCookieService stigmaCookieService,
                                      final StigmaService stigmaService) {
        this.request = request;
        this.response = response;
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaService = stigmaService;
    }

    @Override
    public void handle(final LoginResult loginResult, final LoginContext context) {
        final StigmaToken receivedStigma = stigmaCookieService.readStigmaToken(request);
        final StigmaToken newStigma = stigmaService.refreshStigma(receivedStigma, loginResult);

        if (!newStigma.equals(receivedStigma)) {
            stigmaCookieService.writeStigmaToken(response, newStigma);
        }
    }
}
