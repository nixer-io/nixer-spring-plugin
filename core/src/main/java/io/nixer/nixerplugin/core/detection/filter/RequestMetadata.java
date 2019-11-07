package io.nixer.nixerplugin.core.detection.filter;

public interface RequestMetadata {

    String USER_AGENT_TOKEN = "nixer.useragent.token";

    String IP_METADATA = "nixer.ip.metadata";
    String PWN_METADATA = "nixer.pwn.metadata";

    //TODO how structure naming
    // nixer.ip.failedLoginOverThreshold vs nixer.failedLoginOverThreshold.ip
    String IP_FAILED_LOGIN_OVER_THRESHOLD = "nixer.ip.failedLoginOverThreshold";
    String USERNAME_FAILED_LOGIN_OVER_THRESHOLD = "nixer.username.failedLoginOverThreshold";
    String USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD = "nixer.useragent.failedLoginOverThreshold";

    String GLOBAL_CREDENTIAL_STUFFING = "nixer.cs.global";
}
