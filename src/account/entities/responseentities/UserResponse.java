package account.entities.responseentities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;


@Getter
@AllArgsConstructor
public class UserResponse {


    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Set<String> roles;


    public UserResponse(String name, String lastname, String email, Set<String> roles) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.roles = roles;
    }
}
