package io.nixer.nixerplugin.core.detection.filter.behavior;

import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;

import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.GLOBAL_CREDENTIAL_STUFFING;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_METADATA;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD;
import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;

/**
 * Static methods for returning conditions based on facts
 */
public class Conditions {

    public static boolean isBlacklistedIp(Facts facts) {
        IpMetadata ipMetadata = (IpMetadata) facts.getFact(RequestMetadata.IP_METADATA);
        return ipMetadata != null && ipMetadata.isBlacklisted();
    }

    public static boolean isGlobalCredentialStuffing(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(RequestMetadata.GLOBAL_CREDENTIAL_STUFFING));
    }

    public static boolean isIpLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD));
    }

    public static boolean isUserAgentLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD));
    }

    public static boolean isUsernameLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD));
    }
}
