package account.security;

import account.logging.LogInfoAggregator;
import account.logging.LoggingActions;
import account.services.LoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    private final ObjectMapper objectMapper;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {


        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");


        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("status", HttpStatus.FORBIDDEN.value());
        data.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        data.put("message", "Access Denied!");
        data.put("path", request.getRequestURI());

        String username = getUsername();
        LogInfoAggregator.setUserNameForLogging(username);
        loggerService.processLogEvents(LoggingActions.ACCESS_DENIED);


        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));

    }


    private String getUsername() {
        // Retrieve the authentication object from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Retrieve the username from the authentication object
            return authentication.getName();
        }

        // If the authentication object is not available or not authenticated, return null or handle accordingly
        return null;
    }
}
