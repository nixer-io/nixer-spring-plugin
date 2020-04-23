package io.nixer.nixerplugin.core.detection.filter.fingerprint;

import java.security.SecureRandom;

import org.springframework.util.StringUtils;

public class FingerprintGenerator {

    public String newFingerprint() {
        return generateSessionId("nixerfp", 16);
    }

    public boolean verifyOwnership(final String fingerprint) {
        if (StringUtils.hasText(fingerprint)) {
            final String suffix = fingerprint.split(".")[1]; // todo:NPE
            if ("nixerfp".equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    // copied from Catalina org.apache.catalina.util.StandardSessionIdGenerator
    private String generateSessionId(final String route, final int sessionIdLength) {
        byte[] random = new byte[16];
        StringBuilder buffer = new StringBuilder(2 * sessionIdLength + 20);
        int resultLenBytes = 0;

        while (resultLenBytes < sessionIdLength) {
            this.getRandomBytes(random);

            for (int j = 0; j < random.length && resultLenBytes < sessionIdLength; ++j) {
                byte b1 = (byte) ((random[j] & 240) >> 4);
                byte b2 = (byte) (random[j] & 15);
                if (b1 < 10) {
                    buffer.append((char) (48 + b1));
                } else {
                    buffer.append((char) (65 + (b1 - 10)));
                }

                if (b2 < 10) {
                    buffer.append((char) (48 + b2));
                } else {
                    buffer.append((char) (65 + (b2 - 10)));
                }

                ++resultLenBytes;
            }
        }

        if (route != null && route.length() > 0) {
            buffer.append('.').append(route);
        }

        return buffer.toString();
    }

    private void getRandomBytes(byte[] bytes) {
        new SecureRandom().nextBytes(bytes);
    }
}
