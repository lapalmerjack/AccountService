package account.security;

import account.entities.LogInfoAggregator;
import account.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component("delegatedAuthenticationEntryPoint")
public class RestAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthEntryPoint.class);

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        System.out.println("SUP");
        String noBasicAuthError = "Full authentication is required to access this resource";
        if (authException.getMessage().equals(noBasicAuthError)) {
            // Set response status code and content type
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // Create a JSON object for the error response
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "Authentication failed: " + authException.getMessage());

            // Convert the error response to JSON and write it to the response body
            objectMapper.writeValue(response.getWriter(), errorResponse);
        } else {
            System.out.printf("ERRORS: " + authException);
            resolver.resolveException(request, response, null, authException);

        }





    }
}