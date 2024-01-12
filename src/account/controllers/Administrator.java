package account.controllers;


import account.entities.User;
import account.entities.responseentities.LockAndUnLockEntity;
import account.logging.LogInfoAggregator;
import account.logging.LoggingActions;
import account.entities.responseentities.GrantAndRemoveEntity;
import account.entities.responseentities.UserResponse;
import account.security.customsecurityconfig.UserDetailsImpl;
import account.services.AdminService;
import account.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
public class Administrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Administrator.class);

    @Autowired
    private final AdminService adminService;

    @Autowired
    private final LoggerService loggerService;

    @Autowired
    HttpServletRequest request;

    public Administrator(AdminService adminService, LoggerService loggerService) {
        this.adminService = adminService;
        this.loggerService = loggerService;

    }


    @GetMapping("/user/")
    public ResponseEntity<List<UserResponse>> getUser(@Valid @AuthenticationPrincipal UserDetailsImpl user) {
        System.out.println("GETTING USERS");
        List<UserResponse> users = adminService.getUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @DeleteMapping("/user/{email}")
    public ResponseEntity<Map<String, String>> deleteUser(@Valid @AuthenticationPrincipal UserDetailsImpl user,
                                        @PathVariable String email) {


        adminService.deleteUserFromDataBase(user.getUsername(), email);
        Map<String, String> deletedUser = new HashMap<>();
        deletedUser.put("user", email);
        deletedUser.put("status", "Deleted successfully!");
        LOGGER.info("USER DELETED");

        LogInfoAggregator.setUserNameForLogging(user.getUsername());
        LogInfoAggregator.setObjectInfoForLogging(email);
        loggerService.processLogEvents(LoggingActions.DELETE_USER);

        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @PutMapping("/user/role")
    public ResponseEntity<UserResponse> updateUserRoles(@Valid @AuthenticationPrincipal UserDetailsImpl user,
                                            @RequestBody GrantAndRemoveEntity roleToRemoveOrGrant) throws UnsupportedEncodingException {


        UserResponse userWithUpdatedRoles = adminService.updateUserRole(roleToRemoveOrGrant);
        System.out.println("THE CONTROLLER: " + userWithUpdatedRoles.getRoles());

        LogInfoAggregator.setUserNameForLogging(user.getUsername());
        LogInfoAggregator.setObjectInfoForLogging(roleToRemoveOrGrant.getUser() + " " + roleToRemoveOrGrant.getRole());
        LoggingActions correctActionForLogger = returnLoggingActionForRoles(roleToRemoveOrGrant.getOperation());
        loggerService.processLogEvents(correctActionForLogger);

        return new ResponseEntity<>(userWithUpdatedRoles, HttpStatus.OK);
    }

    @PutMapping("/user/access")
    public ResponseEntity<Map<String, String>> updateUserLock(@RequestBody LockAndUnLockEntity lockAndUnLockEntity) {
        System.out.println("LOCKANDUNLOCK = " + lockAndUnLockEntity.getUser() + lockAndUnLockEntity.getOperation());
        User userWithUpdatedLock = adminService.updateUserLockCondition(lockAndUnLockEntity);
        Map<String, String> configuredUser = new HashMap<>();
        configuredUser.put("status", "User " + userWithUpdatedLock.getEmail() +
               " " + lockAndUnLockEntity.getOperation().name().toLowerCase() + "ed!");
        LogInfoAggregator.setObjectInfoForLogging(userWithUpdatedLock.getEmail());
        LoggingActions correctLoggingAction = returnLoggingActionForLocks(lockAndUnLockEntity.getOperation());
        loggerService.processLogEvents(correctLoggingAction);

        return new ResponseEntity<>(configuredUser, HttpStatus.OK);
    }

    public LoggingActions returnLoggingActionForLocks(LockAndUnLockEntity.Operations operation) {
        return operation.name().equals("LOCK") ? LoggingActions.LOCK_USER : LoggingActions.UNLOCK_USER;
    }

    public LoggingActions returnLoggingActionForRoles(GrantAndRemoveEntity.Operations operation) {

        return operation.name().equals("REMOVE")  ? LoggingActions.REMOVE_ROLE : LoggingActions.GRANT_ROLE;
    }


}
