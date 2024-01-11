package account.entities.responseentities;

import account.entities.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


@Getter
public class UserResponse {


    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Set<String> roles;


    public UserResponse(Long id, String name, String lastname, String email, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = new TreeSet<>(Comparator.naturalOrder());
        this.roles.addAll(roles);
    }
}
