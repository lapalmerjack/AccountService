package account.security;

import account.entities.User;
import account.logging.LogInfoAggregator;
import account.logging.LoggingActions;
import account.repositories.UserRepository;

import account.security.customsecurityconfig.CustomAccessDeniedHandler;
import account.security.customsecurityconfig.UserDetailsImpl;
import account.security.filters.ExtractRequestInfoFilter;
import account.services.LoggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private RestAuthEntryPoint restAuthEntryPoint;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggerService loggerService;


    private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthEntryPoint.class);


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LOGGER.info("Beginning Security");
        http
                .csrf(csrf -> {
                    csrf.disable();
                    csrf.ignoringRequestMatchers(PathRequest.toH2Console());
                })
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PathRequest.toH2Console()).permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/signup", "/actuator/shutdown")
                            .permitAll()
                            .requestMatchers("/api/admin/user/**").hasAnyAuthority("ROLE_ADMINISTRATOR")
                            .requestMatchers("/api/acct/payments").hasAnyAuthority("ROLE_ACCOUNTANT")
                            .anyRequest().authenticated();
                })
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(extractRequestInfoFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic().authenticationEntryPoint(restAuthEntryPoint);

        LOGGER.info("Finished security");

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(new ObjectMapper());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());

        return provider;
    }

    @Bean
    @Order(1)
    public ExtractRequestInfoFilter extractRequestInfoFilter() {
        return new ExtractRequestInfoFilter();
    }


    @Bean
    public UserDetailsService userDetailsService() {

        return email -> {
            LOGGER.info("Checking if user exists");
            User databaseUser = userRepository.findByEmailIgnoreCase(email.toLowerCase())
                    .orElseThrow(() -> {
                        LOGGER.error("User was {} not found", email.toLowerCase());
                        return new UsernameNotFoundException("User not found");
                    });
            LOGGER.info("User found {}", databaseUser.getLastname());

            if (!databaseUser.getIsAccountNotLocked()) {

                throw new LockedException("User account is locked");
            }

            return new UserDetailsImpl(databaseUser);
        };


    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}