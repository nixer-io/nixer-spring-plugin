package io.nixer.nixerplugin.core.fingerprint.store_draft;

import org.springframework.context.event.EventListener;

public class FingerprintActivityListener {

    private final FingerprintStore fingerprintStore;

    public FingerprintActivityListener(final FingerprintStore fingerprintStore) {
        this.fingerprintStore = fingerprintStore;
    }

    @EventListener
    public void handleEvent(FingerprintMetadata fingerprintMetadata) {
        //save to metrics
        fingerprintStore.save(
                fingerprintMetadata.getFingerprint(),
                "request_uuid",
                "url",
                "IP"
        );

    }
}
