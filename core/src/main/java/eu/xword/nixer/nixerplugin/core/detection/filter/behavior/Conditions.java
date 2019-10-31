package eu.xword.nixer.nixerplugin.core.detection.filter.behavior;

import eu.xword.nixer.nixerplugin.core.detection.filter.ip.IpMetadata;

import static eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata.GLOBAL_CREDENTIAL_STUFFING;
import static eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD;
import static eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata.IP_METADATA;
import static eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata.USERNAME_FAILED_LOGIN_OVER_THRESHOLD;
import static eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata.USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD;

/**
 * Static methods for returning conditions based on facts
 */
public class Conditions {

    public static boolean isBlacklistedIp(Facts facts) {
        IpMetadata ipMetadata = (IpMetadata) facts.getFact(IP_METADATA);
        return ipMetadata != null && ipMetadata.isBlacklisted();
    }

    public static boolean isGlobalCredentialStuffing(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(GLOBAL_CREDENTIAL_STUFFING));
    }

    public static boolean isIpLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(IP_FAILED_LOGIN_OVER_THRESHOLD));
    }

    public static boolean isUserAgentLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD));
    }

    public static boolean isUsernameLoginOverThreshold(Facts facts) {
        return Boolean.TRUE.equals(facts.getFact(USERNAME_FAILED_LOGIN_OVER_THRESHOLD));
    }
}
