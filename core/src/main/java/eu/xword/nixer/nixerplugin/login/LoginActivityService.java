package eu.xword.nixer.nixerplugin.login;

import java.util.List;

import eu.xword.nixer.nixerplugin.rules.RulesEngine;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityService {

    private final List<LoginActivityRepository> loginActivityRepositories;

    private final RulesEngine rulesEngine;

    public LoginActivityService(final List<LoginActivityRepository> loginActivityRepositories, final RulesEngine rulesEngine) {
        this.loginActivityRepositories = loginActivityRepositories;
        this.rulesEngine = rulesEngine;
    }

    public void handle(final LoginResult loginResult, final LoginContext context) {
        //TODO extract keeping track of stats to dedicated place
        loginActivityRepositories.forEach(it -> it.reportLoginActivity(loginResult, context));

        rulesEngine.onLogin(loginResult, context);
    }

}
