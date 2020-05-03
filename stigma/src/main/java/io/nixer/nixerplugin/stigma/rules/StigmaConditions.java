package io.nixer.nixerplugin.stigma.rules;

import io.nixer.nixerplugin.core.detection.filter.behavior.Facts;
import io.nixer.nixerplugin.stigma.StigmaConstants;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import org.springframework.util.Assert;

/**
 * Created on 27/04/2020.
 *
 * @author Grzegorz Cwiak
 */
public class StigmaConditions {

    public static boolean isStigmaRevoked(Facts facts) {
        Assert.notNull(facts, "facts must not be null");

        final StigmaDetails stigmaDetails = (StigmaDetails) facts.getFact(StigmaConstants.STIGMA_METADATA_ATTRIBUTE);

        return stigmaDetails != null && stigmaDetails.isRevoked();
    }
}
