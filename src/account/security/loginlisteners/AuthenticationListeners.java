package account.security.loginlisteners;


import account.entities.LogInfoAggregator;
import account.services.LoggerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;



@Component
public class AuthenticationListeners  {


    private final LoggerService loggerService;

    @Autowired
    HttpServletRequest request;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationListeners.class);

    public AuthenticationListeners(LoggerService loggerService) {
        this.loggerService = loggerService;
    }


    @EventListener
    public void successfulLogin(AuthenticationSuccessEvent event) throws IOException {


        LogInfoAggregator.setUserNameForLogging(event.getAuthentication().getPrincipal().toString()
        );

        // Access the cached request body using getContentAsByteArray or getContentAsString




        UserDetails principal = (UserDetails) event.getAuthentication().getPrincipal();
        LOGGER.info("Success with principal {}", principal.getUsername());
    }


    @EventListener
    public void failureOnLogin(AbstractAuthenticationFailureEvent event) throws ServletException, IOException {



        LOGGER.error("FAILURE LOGGING IN {}", event.getAuthentication().getPrincipal());
        System.out.println(LogInfoAggregator.getUrlPath());
       // loggerService.processLogEvents(LoggingActions.LOGIN_FAILED);
    }


}
