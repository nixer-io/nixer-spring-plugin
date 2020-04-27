package io.nixer.nixerplugin.stigma.detection;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.stigma.decision.StigmaService;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.login.StigmaCookieService;

import static io.nixer.nixerplugin.stigma.StigmaConstants.STIGMA_METADATA_ATTRIBUTE;

/**
 * Created on 24/04/2020.
 *
 * @author Grzegorz Cwiak
 */
public class StigmaExtractingFilter extends MetadataFilter {

    private final StigmaCookieService stigmaCookieService;

    private final StigmaService stigmaService;

    public StigmaExtractingFilter(final StigmaCookieService stigmaCookieService, final StigmaService stigmaService) {
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaService = stigmaService;
    }

    @Override
    protected void apply(final HttpServletRequest request) {

        final RawStigmaToken rawStigmaToken = stigmaCookieService.readStigmaToken(request);

        final StigmaDetails stigmaDetails = stigmaService.findStigmaDetails(rawStigmaToken);

        if (stigmaDetails != null) {
            request.setAttribute(STIGMA_METADATA_ATTRIBUTE, stigmaDetails);
        }
    }
}
