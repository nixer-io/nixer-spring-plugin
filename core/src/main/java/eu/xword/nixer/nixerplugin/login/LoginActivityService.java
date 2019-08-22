package eu.xword.nixer.nixerplugin.login;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityService {

    @Autowired
    private List<LoginActivityRepository> loginActivityRepositories;

    public void handle(final LoginResult loginResult, final LoginContext context) {
        //TODO extract keeping track of stats to dedicated place
        loginActivityRepositories.forEach(it -> it.reportLoginActivity(loginResult, context));
    }

}
