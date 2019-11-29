package io.nixer.nixerplugin.core.login;

/**
 * Created on 29/11/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public interface LoginActivityHandler {

    // TODO consider merging the arguments into a single one
    void handle(LoginResult loginResult, LoginContext context);
}
