package account.services;

import account.entities.Role;
import account.entities.User;
import account.entities.enums.BreachedPasswords;
import account.entities.responseentities.UserResponse;
import account.error.customexceptions.users.*;
import account.repositories.UserRepository;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);



    @Autowired
    PasswordEncoder encoder;


    @Autowired
    UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserResponse registerUser(User user) {

        LOGGER.info("Registering new user to database");
        user.setEmail(user.getEmail().toLowerCase());


        checkForExistingUser(user);

        LOGGER.info("Password length: " + user.getPassword().length());

        isPasswordLengthCorrect(user.getPassword());
        checkForBannedPassword(user.getPassword());

        LOGGER.info("Setting user password");
        user.setPassword(encoder.encode(user.getPassword()));

        User updatedUserRole = addRolesForNewUser(user);

        LOGGER.info("Saving user in database");
        User savedUser = userRepository.save(updatedUserRole);

        Set<String> roles = new HashSet<>();
        savedUser.getRoles().forEach(role -> roles.add(role.getRole()));

        return new UserResponse(savedUser.getId(), savedUser.getName(),
                savedUser.getLastname(), savedUser.getEmail(), roles );

    }

    private User addRolesForNewUser (User user) {
        User savedUser = new User(user.getName(), user.getLastname(), user.getEmail(), user.getPassword());

        String roleName = userRepository.findAll().isEmpty() ? "ROLE_ADMINISTRATOR" : "ROLE_USER";
        savedUser.addRole(new Role(roleName));

      return savedUser;
    }


    private void checkForExistingUser(User user) {
        Optional<User> databaseUser = userRepository.
                findByEmailIgnoreCase(user.getEmail());

        databaseUser.ifPresent(u -> { throw new UserFoundException(); });
    }


    public User changePassword (UserDetails user, String newPassword) {
        User fetchedUser = userRepository.findByEmailIgnoreCase(user.getUsername())
                .orElseThrow(UserNotFoundException::new);
        System.out.println(fetchedUser.getId() + "is not null");
        isPasswordLengthCorrect(newPassword);
        checkForBannedPassword(newPassword);
        isNewPasswordIdentical(fetchedUser.getPassword(), newPassword);


        return updateUserPassword(fetchedUser, newPassword);
    }

    private void checkForBannedPassword(String newPassword) {
        boolean bannedPassWord = Arrays.stream(BreachedPasswords.values())
                .anyMatch((t) -> t.getBreachedPassword().equals(newPassword));

        if (bannedPassWord) {
            LOGGER.error("Throwing exception for banned password");
            throw new PasswordMatchesBannedPassword();
        }
    }

    private void isPasswordLengthCorrect(String password) {

        if(password.length() < 12) {
            LOGGER.info("Throwing Minimum password exception");
            throw new MinimumPasswordLengthException();
        }

    }

    private void isNewPasswordIdentical(String oldPassWord, String newPassword) {
        boolean match = encoder.matches(newPassword, oldPassWord);

        if (match) {
            throw new NewPasswordMatchesOldPassword();
        }
    }

    private User updateUserPassword(User user, String newPassword) {
        User updatedUser = userRepository.getReferenceById(user.getId());
        updatedUser.setPassword(encoder.encode(newPassword));

        userRepository.save(updatedUser);

        return updatedUser;


    }

    public User getEmployeeInfo(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(UserNotFoundException::new);
    }
}