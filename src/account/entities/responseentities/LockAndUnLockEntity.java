package account.entities.responseentities;




import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockAndUnLockEntity {

    private String user;
    private Operations operations;

    public enum Operations {
        LOCK, UNLOCK
    }
}
