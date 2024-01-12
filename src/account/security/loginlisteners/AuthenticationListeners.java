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
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;



@Component
public class AuthenticationListeners  {


    private final LoggerService loggerService;

    private final UserService userService;


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
    public void failureOnLogin(AbstractAuthenticationFailureEvent event)  {

        System.out.println("FAILURE TO LOG IN BRO PRINCIPAL " + event.getAuthentication().getPrincipal());

      

        String email = event.getAuthentication().getPrincipal().toString();
        LogInfoAggregator.setUserNameForLogging(email);
        loggerService.processLogEvents(LoggingActions.LOGIN_FAILED);

        LOGGER.error("FAILURE LOGGING IN {}", event.getAuthentication().getPrincipal());

       boolean user =  userService.isUserInDataBase(email);
        System.out.println("User in database: " + user);
       if (user) {
           handleBruteForceCheckIfUserExists(email);
       }

    }

    @EventListener
    public void handleInternalAuthServiceException(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        LOGGER.error("Authentication failure for locked user: {}", username);


    }
    private void handleBruteForceCheckIfUserExists(String email) {
        System.out.println("HANDLING BRUTE FORCE ");
        User user = userService.retrieveEmployee(email);
        System.out.println("IS ACCOUNT NOT LOCKED? " + user.getIsAccountNotLocked());
        if(user.getIsAccountNotLocked()) {
            System.out.println("GOING IN DAT BRUTE BRUTE FORCE FOR !" + user.getEmail());
            configureCheckAttemptsAndLock(user);
        }
    }
    public void configureCheckAttemptsAndLock(User user) {
        System.out.println(user.getLoginAttempts() + " THESE ARE THE ATTEMPTS");

        if (user.getLoginAttempts() < UserService.MAX_FAILED_ATTEMPTS) {
            System.out.println("UPPING FAILED ATTEMPT FOR " + user.getEmail());
            userService.increaseFailedLoginAttempts(user);

        } else {
            System.out.println("LOCKING THIS CHUMP " + user.getEmail());

            userService.lockUser(user);
            LogInfoAggregator.setObjectInfoForLogging(user.getEmail());
            loggerService.processLogEvents(LoggingActions.BRUTE_FORCE);
            loggerService.processLogEvents(LoggingActions.LOCK_USER);

            throw new LockedException("The user has been locked after three attempts");
        }
    }

}
