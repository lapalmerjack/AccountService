package account.controllers;

import account.entities.responseentities.UserResponse;
import account.services.UserService;
import account.entities.User;
import jakarta.validation.Valid;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/auth")
public class Authentication {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Authentication.class);

    public Authentication(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerNewUser (@Valid @RequestBody User user) {


        UserResponse registeredUser = userService.registerUser(user);

        return new ResponseEntity<>(registeredUser, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/users")
    public ResponseEntity<User> getEmployeeInfo (@AuthenticationPrincipal UserDetails user) {
        LOGGER.info("Retrieving user details");


        User fetchedUser = userService.getEmployeeInfo(user.getUsername());

        return new ResponseEntity<>(fetchedUser, HttpStatusCode.valueOf(200));

    }



    @PostMapping("/changepass")
    public ResponseEntity<Map<String, String>>
    changeUserPassWord(@Valid @AuthenticationPrincipal UserDetails user,
                       @RequestBody Map<String, String> newPassword) {

        LOGGER.info("changing user password");

        User updatedUser = userService.changePassword(user, newPassword.get("new_password"));

        LOGGER.info("The user password has been updated");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("email", updatedUser.getEmail());
        map.put("status", "The password has been updated successfully" );


        return new ResponseEntity<>(map, HttpStatusCode.valueOf(200));

    }

}