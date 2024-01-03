package account.services;

import account.entities.GrantAndRemoveEntity;
import account.entities.Role;
import account.entities.User;
import account.entities.responseentities.UserResponse;
import account.error.customexceptions.admin.*;
import account.error.customexceptions.users.UserNotFoundException;
import account.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);


    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }
    @Transactional
    public UserResponse updateUserRole(GrantAndRemoveEntity grantAndRemoveEntity) {

        LOGGER.info("Retrieving user for update: " + grantAndRemoveEntity.getUser());
        User user = retrieveUser(grantAndRemoveEntity.getUser().toLowerCase());

        checkRoleName(grantAndRemoveEntity.getRole());
        User updatedUser = handleUserOperation(user, grantAndRemoveEntity);

        userRepository.save(updatedUser);
        LOGGER.info("User has been updated and saved: " + user.getEmail());


        return setUserResponse(user.getId(), updatedUser);
    }

    public void deleteUserFromDataBase(String administratorEmail, String email) {

        if (administratorEmail.equals(email)) {
            LOGGER.error("Administrator may not delete itself");
            throw new UserCantDeleteItSelfException();
        }

        userRepository.deleteByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public List<UserResponse> getUsers() {

        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getLastname(),
                        user.getEmail(),
                        user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet())
                ))
                .toList();
    }

    private void checkRoleName(String role) {

        LOGGER.info("THIS IS THE ROLE: " + role);
        String regex = "ADMINISTRATOR|USER|ACCOUNTANT";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(role);

        if (!matcher.matches()) {
            throw new RoleDoesNotExist();
        }
    }

    private User handleUserOperation(User user, GrantAndRemoveEntity grantAndRemoveEntity) {

        return switch(grantAndRemoveEntity.getOperation()) {
            case GRANT -> grantNewRole(user, grantAndRemoveEntity.getRole());
            case REMOVE -> removeRole(user, grantAndRemoveEntity.getRole());
        };
    }

    private User grantNewRole(User user, String role) {

        LOGGER.info("ROLE TO GRANT " + role);

        checkIfUserIsAdministrator(user);
        checkIfOneUserHasUniqueRole(role);

        LOGGER.info("Checks passed, adding new role");

        Set<Role> updatedRoles = user.getRoles();
        updatedRoles.add(new Role("ROLE_" + role));

        return returnUpdatedUser(user, updatedRoles);
    }

    private User removeRole(User user, String role) {

       checkIfTheRoleExistForUserBeforeRemoval(user.getRoles(), role);

        if ("ADMINISTRATOR".equals(role)) {
            LOGGER.error("Admin role can never be deleted");
            throw new UserCantDeleteItSelfException();
        }

        Set<Role> updatedRoles = removeRoleAndReturnUpdatedSet(user, role);

        return returnUpdatedUser(user, updatedRoles);

    }

    private Set<Role> removeRoleAndReturnUpdatedSet(User user, String role) {

        Set<Role> updatedRoles = user.getRoles()
                .stream()
                .filter(r -> !r.getRole().equals("ROLE_"+ role))
                .collect(Collectors.toSet());
        checkIfAtLeastOneRole(updatedRoles);
        return updatedRoles;
    }

    private void checkIfTheRoleExistForUserBeforeRemoval(Set<Role> roles, String role) {

        boolean doesRoleExistForUser = roles
                .stream()
                .anyMatch(r -> r.getRole().equals("ROLE_" + role));

        if (!doesRoleExistForUser) {
            LOGGER.error("role does not exist for user");
            throw new RoleDoesNotExistForUser();
        }


    }

    private void checkIfOneUserHasUniqueRole(String role) {
            String updatedRole = "ROLE_" + role;
            Set<String> userRoles = getRolesFromAllUsers();

        if (userRoles.contains( updatedRole)) {
                LOGGER.error("Thrown because one user already has the role of " + role);
                throw new RoleCombinationException();
            }
    }
    private Set<String> getRolesFromAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .flatMap(user -> user.getRoles().stream())
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }

    private User returnUpdatedUser(User user, Set<Role> updatedRoles) {
        return new User(user.getId(),user.getName(),user.getLastname(),
                user.getEmail(), user.getPassword(), updatedRoles);
    }

    private User retrieveUser(String email) {
        return userRepository
                .findByEmailIgnoreCase(email).orElseThrow(
                        UserNotFoundException::new);
    }


    private UserResponse setUserResponse(Long id, User updatedUser) {
        Set<String> updatedRoles = new HashSet<>();
        updatedUser
                .getRoles()
                .forEach(s -> updatedRoles.add(s.getRole()));
        return new UserResponse(id, updatedUser.getName(), updatedUser.getLastname(),
                updatedUser.getEmail(), updatedRoles);

    }

    private void checkIfAtLeastOneRole(Set<Role> roles) {
        if(roles.isEmpty()) {
            throw new InsufficientRoleCountException();
        }
    }

    private void checkIfUserIsAdministrator(User user) {
        boolean isAdministrator = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toSet())
                .contains("ROLE_ADMINISTRATOR");

        if (isAdministrator) {
            LOGGER.error("Error thrown because user is ADMIN");
            throw new RoleCombinationException();
        }
    }

}
