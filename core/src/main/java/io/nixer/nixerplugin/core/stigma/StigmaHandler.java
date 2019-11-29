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
public class StigmaHandler implements LoginActivityHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final StigmaUtils stigmaUtils;
    private final StigmaService stigmaService;

    public StigmaHandler(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final StigmaUtils stigmaUtils,
                         final StigmaService stigmaService) {
        this.request = request;
        this.response = response;
        this.stigmaUtils = stigmaUtils;
        this.stigmaService = stigmaService;
    }

    @Override
    public void handle(final LoginResult loginResult, final LoginContext context) {
        final StigmaToken receivedStigma = stigmaUtils.findStigma(request);
        final StigmaToken newStigma = stigmaService.refreshStigma(receivedStigma, loginResult);

        if (!newStigma.equals(receivedStigma)) {
            stigmaUtils.setStigmaCookie(response, newStigma);
        }
    }
}
