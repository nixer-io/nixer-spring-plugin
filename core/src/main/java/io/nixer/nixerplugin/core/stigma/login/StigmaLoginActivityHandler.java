package io.nixer.nixerplugin.core.stigma.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.core.login.LoginActivityHandler;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.core.stigma.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.evaluate.StigmaAction;
import io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionEvaluator;

/**
 * Entry point for StigmaToken-based credential stuffing protection mechanism.
 *
 * Created on 28/11/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaLoginActivityHandler implements LoginActivityHandler {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final StigmaCookieService stigmaCookieService;
    private final StigmaActionEvaluator stigmaActionEvaluator;

    public StigmaLoginActivityHandler(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      final StigmaCookieService stigmaCookieService,
                                      final StigmaActionEvaluator stigmaActionEvaluator) {
        this.request = request;
        this.response = response;
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaActionEvaluator = stigmaActionEvaluator;
    }

    @Override
    public void handle(final LoginResult loginResult, final LoginContext ignored) {

        final RawStigmaToken receivedStigmaToken = stigmaCookieService.readStigmaToken(request);

        // FIXME fix this awkward null handling
        final String receivedStigmaTokenValue = receivedStigmaToken != null
                ? receivedStigmaToken.getValue()
                : null;

        final StigmaAction action = loginResult.isSuccess()
                ? stigmaActionEvaluator.onLoginSuccess(receivedStigmaTokenValue)
                : stigmaActionEvaluator.onLoginFail(receivedStigmaTokenValue);

        if (action.isTokenRefreshRequired()) {
            // FIXME include login result and stigma state into the decision
            // and move this logic to proper place
            stigmaCookieService.writeStigmaToken(response, new RawStigmaToken((action.getStigmaToken()))); // TODO remove wrapping
        }
    }
}
