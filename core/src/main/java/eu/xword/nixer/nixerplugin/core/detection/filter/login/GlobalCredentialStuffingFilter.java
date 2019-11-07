package eu.xword.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import eu.xword.nixer.nixerplugin.core.detection.registry.CredentialStuffingRegistry;
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