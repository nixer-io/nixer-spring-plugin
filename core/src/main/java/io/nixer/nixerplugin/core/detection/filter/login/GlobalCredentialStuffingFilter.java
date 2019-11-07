package io.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.CredentialStuffingRegistry;
import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import org.springframework.util.Assert;

public class GlobalCredentialStuffingFilter extends MetadataFilter {

    private final CredentialStuffingRegistry credentialStuffingRegistry;

    public GlobalCredentialStuffingFilter(final CredentialStuffingRegistry credentialStuffingRegistry) {
        Assert.notNull(credentialStuffingRegistry, "CredentialStuffingRegistry must not be null");
        this.credentialStuffingRegistry = credentialStuffingRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final boolean isActive = credentialStuffingRegistry.isCredentialStuffingActive();
        request.setAttribute(RequestMetadata.GLOBAL_CREDENTIAL_STUFFING, isActive);
    }
}
