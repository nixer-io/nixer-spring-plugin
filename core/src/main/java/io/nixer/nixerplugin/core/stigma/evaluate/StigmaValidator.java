package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;

/**
 * Created on 21/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValidator {

    public boolean isValid(@Nullable final StigmaData stigmaData) {
        return stigmaData != null
                && stigmaData.getStatus() == StigmaStatus.ACTIVE;
    }
}
