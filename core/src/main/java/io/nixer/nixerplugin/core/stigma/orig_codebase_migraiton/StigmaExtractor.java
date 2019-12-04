package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import javax.annotation.Nullable;

/**
 * Created on 2019-06-28.
 *
 * @author gcwiak
 */
public interface StigmaExtractor {
    @Nullable
    StigmaData tryExtractingStigma(@Nullable String rawToken);
}
