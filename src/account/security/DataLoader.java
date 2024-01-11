package account.security;

import account.controllers.Administrator;
import account.entities.Role;
import account.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataLoader {

    private final RoleRepository roleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    public DataLoader(RoleRepository roleRepository) throws Exception {
        this.roleRepository = roleRepository;

    }

    @PostConstruct
    private void init() throws Exception {
        createRoles();
    }

    private void createRoles() throws Exception {

        List<Role> roles = roleRepository.findAll();
        System.out.println(roles);

        try {
            if(roles.isEmpty()) {
               addProperRoles();
            } else {
                roles.forEach(this::saveIfNotExist);
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());

        }
    }

    private void addProperRoles() {

        roleRepository.save(new Role("ROLE_ADMINISTRATOR"));
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_ACCOUNTANT"));
        roleRepository.save(new Role("ROLE_AUDITOR"));
        System.out.println("ROLES ADDED");
    }

    private void saveIfNotExist(Role role) {
        if (!roleRepository.existsByRole(role.getRole())) {
           LOGGER.info("Adding non-existent role");
            roleRepository.save(new Role("ROLE_" + role.getRole()));

        }
    }
}