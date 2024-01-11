package account.security.loginlisteners;


import account.entities.User;
import account.logging.LogInfoAggregator;
import account.logging.LoggingActions;
import account.services.LoggerService;
import account.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class AuthenticationListeners  {


    private final LoggerService loggerService;

    private final UserService userService;

    @Autowired
    HttpServletRequest request;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationListeners.class);

    public AuthenticationListeners(LoggerService loggerService, UserService userService) {
        this.loggerService = loggerService;
        this.userService = userService;
    }


    @EventListener
    public void successfulLogin(AuthenticationSuccessEvent event) throws IOException {


        UserDetails principal = (UserDetails) event.getAuthentication().getPrincipal();
        LogInfoAggregator.setUserNameForLogging(principal.getUsername());
        LOGGER.info("Success with principal {}", principal.getUsername());
    }


    @EventListener
    public void failureOnLogin(AbstractAuthenticationFailureEvent event) throws ServletException, IOException {

        System.out.println("FAILURE TO LOG IN BRO PRINCIPAL " + event.getAuthentication().getPrincipal());
        String email = event.getAuthentication().getPrincipal().toString();
        User user = userService.retrieveEmployee(email);
        LogInfoAggregator.setUserNameForLogging(email);

        loggerService.processLogEvents(LoggingActions.LOGIN_FAILED);

        if (user.getIsAccountNotLocked()) {
            configureCheckAttemptsAndLock(user);
        }

        LOGGER.error("FAILURE LOGGING IN {}", event.getAuthentication().getPrincipal());

    }

    public void configureCheckAttemptsAndLock(User user) {

        if (user.getLoginAttempts() < UserService.MAX_FAILED_ATTEMPTS) {

            userService.increaseFailedLoginAttempts(user);

        } else {

            userService.lockUser(user);
            LogInfoAggregator.setObjectInfoForLogging(user.getEmail());
            loggerService.processLogEvents(LoggingActions.BRUTE_FORCE);
            loggerService.processLogEvents(LoggingActions.LOCK_USER);

            throw new LockedException("The user has been locked after three attempts");
        }
    }

}
