package io.nixer.nixerplugin.stigma.detection;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.stigma.decision.StigmaService;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.login.StigmaCookieService;

import static io.nixer.nixerplugin.stigma.StigmaConstants.REVOKED_STIGMA_USAGE_ATTRIBUTE;

/**
 * Created on 24/04/2020.
 *
 * @author Grzegorz Cwiak
 */
public class RevokedStigmaDetectingFilter extends MetadataFilter {

    private final StigmaCookieService stigmaCookieService;

    private final StigmaService stigmaService;

    public RevokedStigmaDetectingFilter(final StigmaCookieService stigmaCookieService, final StigmaService stigmaService) {
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaService = stigmaService;
    }

    @Override
    protected void apply(final HttpServletRequest request) {

        final RawStigmaToken rawStigmaToken = stigmaCookieService.readStigmaToken(request);

        final StigmaDetails stigmaDetails = stigmaService.findStigmaDetails(rawStigmaToken);

        if (isRevoked(stigmaDetails)) {
            request.setAttribute(REVOKED_STIGMA_USAGE_ATTRIBUTE, Boolean.TRUE);
        } else {
            request.setAttribute(REVOKED_STIGMA_USAGE_ATTRIBUTE, Boolean.FALSE);
        }
    }

    private static boolean isRevoked(final StigmaDetails stigmaDetails) {
        return stigmaDetails != null && stigmaDetails.getStatus() == StigmaStatus.REVOKED;
    }
}
