package io.nixer.nixerplugin.core.detection.filter;

public interface RequestMetadata {

    String USER_AGENT_TOKEN = "nixer.useragent.token";

    String IP_METADATA = "nixer.ip.metadata";
    String PWN_METADATA = "nixer.pwn.metadata";

    String IP_FAILED_LOGIN_OVER_THRESHOLD = "nixer.ip.failedLoginOverThreshold";
    String USERNAME_FAILED_LOGIN_OVER_THRESHOLD = "nixer.username.failedLoginOverThreshold";
    String USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD = "nixer.useragent.failedLoginOverThreshold";
    String FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD = "nixer.fingerprint.failedLoginOverThreshold";

    String FINGERPRINT_VALUE = "nixer.fingerprint.fingerprintValue";

    String GLOBAL_CREDENTIAL_STUFFING = "nixer.cs.global";

    String FAILED_LOGIN_RATIO_ACTIVE = "nixer.cs.failedLoginRatio";
}
