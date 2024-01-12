package account.security.filters;

import account.logging.LogInfoAggregator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;





public class ExtractRequestInfoFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String urlPath = request.getRequestURL().toString();

        if(urlPath.contains("api")) {
            System.out.println("ACCESSING THREAT YO");
            LogInfoAggregator.setUrlPathForLogging(urlPath.substring(urlPath.indexOf("/api")));
        }

        filterChain.doFilter(request, response);

    }
}
