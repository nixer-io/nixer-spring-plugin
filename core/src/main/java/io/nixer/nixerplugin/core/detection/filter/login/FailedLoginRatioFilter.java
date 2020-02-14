package io.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.FailedLoginRatioRegistry;
import org.springframework.util.Assert;

public class FailedLoginRatioFilter extends MetadataFilter {

    private final FailedLoginRatioRegistry failedLoginRatioRegistry;

    public FailedLoginRatioFilter(final FailedLoginRatioRegistry failedLoginRatioRegistry) {
        Assert.notNull(failedLoginRatioRegistry, "Failed login ration registry can not be null");
        this.failedLoginRatioRegistry = failedLoginRatioRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final boolean isActive = failedLoginRatioRegistry.isFailedLoginRatioActivated();
        request.setAttribute(RequestMetadata.FAILED_LOGIN_RATIO_ACTIVE, isActive);
    }
}
