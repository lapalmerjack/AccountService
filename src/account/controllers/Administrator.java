package account.controllers;


import account.entities.responseentities.GrantAndRemoveEntity;
import account.entities.responseentities.UserResponse;
import account.security.UserDetailsImpl;
import account.services.AdminService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    public Administrator(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/user/")
    public ResponseEntity<List<UserResponse>> getUser(@Valid @AuthenticationPrincipal UserDetailsImpl user) {
        System.out.println("GETTING USERS");
        List<UserResponse> users = adminService.getUsers();

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@Valid @AuthenticationPrincipal UserDetailsImpl user,
                                        @PathVariable String email) {


        adminService.deleteUserFromDataBase(user.getUsername(), email);
        Map<String, String> deletedUser = new HashMap<>();
        deletedUser.put("user", email);
        deletedUser.put("status", "Deleted successfully!");
        LOGGER.info("USER DELETED");
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @PutMapping("/user/role")
    public ResponseEntity<UserResponse> updateUserRoles(@Valid @AuthenticationPrincipal UserDetailsImpl user,
                                            @RequestBody GrantAndRemoveEntity roleToRemoveOrGrant) {

        LOGGER.info("Beginning updating roles");
        UserResponse userWithUpdatedRoles = adminService.updateUserRole(roleToRemoveOrGrant);
        return new ResponseEntity<>(userWithUpdatedRoles, HttpStatus.OK);
    }
}
