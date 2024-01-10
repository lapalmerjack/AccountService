package account.security.filters;

import account.entities.LogInfoAggregator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;





public class ExtractRequestInfoFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        System.out.println(request.getRequestURL() + " THIS IS MY URL PATH");
        String urlPath = request.getRequestURL().toString();
        LogInfoAggregator.setUrlPathForLogging(urlPath.substring(urlPath.indexOf("/api")));
        filterChain.doFilter(request, response);

    }
}
