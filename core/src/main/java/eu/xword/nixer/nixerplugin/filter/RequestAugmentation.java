package eu.xword.nixer.nixerplugin.filter;

public class RequestAugmentation {

    public static final String IP_METADATA = "nixer.ip.metadata";
    public static final String PWN_METADATA = "nixer.pwn.metadata";

    //TODO how structure naming
    // nixer.ip.failedLoginOverThreshold vs nixer.failedLoginOverThreshold.ip
    public static final String IP_FAILED_LOGIN_OVER_THRESHOLD = "nixer.ip.failedLoginOverThreshold";
    public static final String USERNAME_FAILED_LOGIN_OVER_THRESHOLD = "nixer.username.failedLoginOverThreshold";
    public static final String USER_AGENT_FAILED_LOGIN_OVER_THRESHOLD = "nixer.user-agent.failedLoginOverThreshold";

    public static final String GLOBAL_CREDENTIAL_STUFFING = "nixer.cs.global";
}
