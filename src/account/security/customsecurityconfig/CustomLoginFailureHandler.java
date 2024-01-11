package account.security.customsecurityconfig;

import account.entities.User;
import account.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("FAILURE TO LOG IN BRO");
        String email = request.getParameter("email");
        User user = userService.retrieveEmployee(email);

        if(!user.getIsAccountNotLocked()) {
            configureCheckAttemptsAndLock(user);
        }

        super.onAuthenticationFailure(request, response, exception);
    }

    public void configureCheckAttemptsAndLock(User user) {
        if(user.getLoginAttempts() < UserService.MAX_FAILED_ATTEMPTS - 1) {
            userService.increaseFailedLoginAttempts(user);
        } else {
            userService.lockUser(user);
            throw new LockedException("The user has been locked after three attempts");
        }
    }
}
