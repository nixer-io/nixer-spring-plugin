package eu.xword.nixer.nixerplugin.login.counts;

public interface IpCountStore {

    int failedLoginByIp(String ip);
}
