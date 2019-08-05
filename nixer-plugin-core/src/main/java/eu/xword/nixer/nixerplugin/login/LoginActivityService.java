package eu.xword.nixer.nixerplugin.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class LoginActivityService {

    //TODO make thresholds configurable
    private static final int LOGIN_FAILED_BY_IP_THRESHOLD = 50;
    private static final int LOGIN_FAILED_BY_USER_THRESHOLD = 5;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private LoginActivityRepository loginActivityRepository;

    public void handle(final LoginResult loginResult, final LoginContext context) {
        //TODO extract keeping track of stats to dedicated place
    }

}
