package account.entities.responseentities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrantAndRemoveEntity {

    private String user;
    private String role;
    private Operations operation;



    public enum Operations {
        GRANT, REMOVE
    }
}
