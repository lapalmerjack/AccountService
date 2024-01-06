package account.security.loginlisteners;


import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessListener> {


    @Override
    public void onApplicationEvent(AuthenticationSuccessListener event) {

    }
}
