package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaData;

/**
 * Created on 2019-06-28.
 *
 * @author gcwiak
 */
public interface StigmaExtractor {
    @Nullable
    StigmaData tryExtractingStigma(@Nullable String rawToken);
}
