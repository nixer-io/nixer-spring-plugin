package eu.xword.nixer.nixerplugin.login;

public interface LoginActivityRepository {

    void reportLoginActivity(LoginResult result, LoginContext loginContext);
}
